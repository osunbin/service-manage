package com.bin.collector;

import com.bin.client.model.ServerFluxDataRequest;
import com.bin.webmonitor.command.LimitCommand;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.naming.GiveCommand;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.model.Response;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalFunction;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Lazy
public class FluxControlCollector {

    private static Logger logger = LoggerFactory.getLogger(FluxControlCollector.class);

    @Autowired
    private CallerUsageDao callerUsageDao;

    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;

    private LoadingCache<Long, ConcurrentHashMap<String, AtomicInteger>> timeFidOrSidCounter = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<Long, ConcurrentHashMap<String, AtomicInteger>>() {
        @Override
        public ConcurrentHashMap<String, AtomicInteger> load(Long key)
                throws Exception {
            return new ConcurrentHashMap<>();
        }
    });

    private LoadingCache<String, Integer> serviceFunctionQuantityLocalCache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
        @Override
        public Integer load(String key) {
            logger.info("[cache_miss_key]key={}", key);
            try {
                if (key.startsWith("s")) {
                    int sid = Integer.parseInt(key.split("_")[0].substring(1));
                    int cid = Integer.parseInt(key.split("_")[1]);
                    CallerUseage callerUseage = callerUsageDao.selectByCidSid(cid, sid);
                    return callerUseage.getQuantity();
                } else {
                    int fid = Integer.parseInt(key.split("_")[0]);
                    int cid = Integer.parseInt(key.split("_")[1]);
                    CallerFunctionUseage request = new CallerFunctionUseage();
                    request.setFid(fid);
                    request.setCid(cid);
                    List<CallerFunctionUseage> callerFunctionUsages = callerFuncUsageDao.selectPage(request, PageRequest.of(0, 1));
                    if (CollectionUtil.isEmpty(callerFunctionUsages)) {
                        return 0;
                    }
                    return callerFunctionUsages.get(0).getQuantity();
                }
            } catch (Exception e) {
                logger.error("op=load_service_function_quantity_error,key={}", key, e);
                return 0;
            }
        }
    });

    @Autowired
    private LocalFunction localFunction;

    @Autowired
    private LocalService localService;

    @Autowired
    private LocalCaller localCaller;

    @Autowired
    private NamingProxy namingProxy;

    @Autowired
    private ServiceDao serviceDao;



    public void export(ServerFluxDataRequest request, String ip) {
        logger.debug("from ip {} request {}", ip, request);
        long time = System.currentTimeMillis();
        // 不是本分钟的请求抛弃
        if (request.getTime() / 1000 / 60 != time / 1000 / 60) {
            return;
        }

        ServiceInstance service = localService.getByName(request.getServiceName());
        if (Objects.isNull(service)) {
            return;
        }

        ConcurrentHashMap<String, AtomicInteger> fidOrSidCount;
        try {
            fidOrSidCount = timeFidOrSidCounter.get(request.getTime());
        } catch (ExecutionException e) {
            return;
        }

        Set<String> requestKeys = new HashSet<>();

        request.getServerFluxDatas().forEach(serverFluxData -> {
            if (serverFluxData.getCallerKeyId() != -1) {
                String functionCaller = generateFunctionCallerKey(serverFluxData.getFunctionId(), (int)serverFluxData.getCallerKeyId());
                String serviceCaller = generateServiceCallerKey(service.getId(), (int)serverFluxData.getCallerKeyId());

                fidOrSidCount.computeIfAbsent(functionCaller, (item) -> new AtomicInteger(0)).addAndGet(serverFluxData.getValue());
                fidOrSidCount.computeIfAbsent(serviceCaller, (item) -> new AtomicInteger(0)).addAndGet(serverFluxData.getValue());

                requestKeys.add(functionCaller);
                requestKeys.add(serviceCaller);
            }
        });

        requestKeys.forEach(key -> {
            AtomicInteger value = fidOrSidCount.get(key);

            int dbCount;
            try {
                dbCount = serviceFunctionQuantityLocalCache.get(key);
            } catch (ExecutionException e) {
                return;
            }
            logger.debug("redis key {} count is {} invoke counter is {}", key, dbCount, value);
            if (dbCount == 0) {
                return;
            }

            boolean isSendLimit = false;
            Caller caller = null;
            StringBuilder message = new StringBuilder();
            String function = null;
            if (dbCount < value.get()) {
                isSendLimit = true;
                int cid, fid;

                if (key.startsWith("s")) {
                    cid = Integer.parseInt(key.split("_")[1]);
                    caller = localCaller.getById(cid);
                    if (caller != null) {

                        sendFluxLimitCommand(service.getServiceName(), caller.getCallerKey(), function, time);

                        message.append("<div class='highlight'>服务级限流告警：已超过阈值，执行限流！</div>\n调用方：").append(caller.getCallerName()).append("\n服务：").append(service.getServiceName()).append("\n实际流量：").append(value.get()).append("\n阈值：").append(dbCount);
                        logger.info("op=limit_service_flux,time={},callerName={},serviceName={},value={},dbCount={}", request.getTime(), caller.getCallerName(), service.getServiceName(), value.get(), dbCount);
                    }
                } else {
                    String[] fidCid = key.split("_");
                    fid = Integer.parseInt(fidCid[0]);
                    cid = Integer.parseInt(fidCid[1]);
                    caller = localCaller.getById(cid);
                    function = localFunction.function(fid);
                    if (caller != null && !StringUtil.isEmpty(function)) {

                        sendFluxLimitCommand(service.getServiceName(), caller.getCallerKey(), function, time);

                        message.append("<div class='highlight'>函数级限流告警：已超过阈值，执行限流！</div>\n调用方：").append(caller.getCallerName()).append("\n服务：").append(service.getServiceName()).append("\n函数：").append(function).append("\n实际流量：").append(value.get()).append("\n阈值：").append(dbCount);
                        logger.info("op=limit_function_flux,time={},callerKey={},serviceName={},function={},value={},dbCount={}", request.getTime(), caller.getCallerName(), service.getServiceName(), function, value.get(), dbCount);
                    }
                }

            }

            if (dbCount * 0.8 <= value.get() && !isSendLimit) {
                int cid, fid;
                if (key.startsWith("s")) {
                    cid = Integer.parseInt(key.split("_")[1]);
                    caller = localCaller.getById(cid);
                    if (caller != null) {
                        message.append("服务级限流预警：已超过80%阈值，可能要触发限流了！\n调用方：").append(caller.getCallerName()).append("\n服务：").append(service.getServiceName()).append("\n实际流量：").append(value.get()).append("\n阈值：").append(dbCount);
                    }
                } else {
                    String[] fidCid = key.split("_");
                    fid = Integer.parseInt(fidCid[0]);
                    cid = Integer.parseInt(fidCid[1]);
                    caller = localCaller.getById(cid);
                    function = localFunction.function(fid);
                    if (caller != null && !StringUtil.isEmpty(function)) {
                        message.append("函数级限流预警：已超过80%阈值，可能要触发限流了！\n调用方：").append(caller.getCallerName()).append("\n服务：").append(service.getServiceName()).append("\n函数：").append(function).append("\n实际流量：").append(value.get()).append("\n阈值：").append(dbCount);
                    }
                }
            }

            if (Objects.nonNull(caller) && StringUtil.isNotEmpty(message)) {
                sendAlertMessage(service, caller, function, message, isSendLimit);
            }

        });
    }


    private void sendAlertMessage(ServiceInstance service, Caller caller, String function, StringBuilder message, boolean isLimit) {

        ServiceInstance dbService = serviceDao.selectByName(service.getServiceName());

        String alertGapKey = generateAlertGapKey(service.getServiceName(), caller.getCallerName(), function, isLimit);

        // TODO  报警
        logger.warn("op=send_flux_control_message,alertGapKey={},message={}", alertGapKey, message);

    }

    private void sendFluxLimitCommand(String serviceName, String callerKey, String functionName, long time) {
        LimitCommand command = new LimitCommand().setTime(time).setCallerKey(callerKey);
        if (StringUtil.isNotEmpty(functionName)) {
            command.setFunction(functionName);
        }

        GiveCommand giveCommand = new GiveCommand().setServerMode(true).setServiceName(serviceName).setContent(command.toContent());
        Response<?> response = namingProxy.execute(giveCommand);
        logger.info("op=end_sendFluxLimitCommand,serviceName={},callerKey={},functionName={},time={},command={},response={}", serviceName, callerKey, functionName, time, command, response);
    }

    private String generateServiceCallerKey(int sid, int cid) {
        return new StringBuilder().append("s").append(sid).append("_").append(cid).toString();
    }

    private String generateFunctionCallerKey(long fid, int cid) {
        return new StringBuilder().append(fid).append("_").append(cid).toString();
    }

    private String generateAlertGapKey(String serviceName, String callerName, String function, boolean isLimited) {
        StringBuilder alertGapKey = new StringBuilder(serviceName).append(Constants.SEPARATOR_UNDERLINE).append(callerName);
        if (StringUtil.isNotEmpty(function)) {
            alertGapKey.append(Constants.SEPARATOR_UNDERLINE).append(function);
        }
        if (isLimited) {
            alertGapKey.append(Constants.SEPARATOR_UNDERLINE).append(Constants.FLUX_LIMIT_TAG);
        }
        return alertGapKey.toString();
    }


}
