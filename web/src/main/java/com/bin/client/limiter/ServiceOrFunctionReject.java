package com.bin.client.limiter;

import com.bin.client.model.ExecutableResult;
import com.bin.client.model.ServiceContext;
import com.bin.client.model.RejectionReason;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.HttpUtil;
import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.common.util.ThreadPool;
import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.model.ServiceRejectMeta;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ServiceOrFunctionReject {

    private static Logger logger = LoggerFactory.getLogger(ServiceOrFunctionReject.class);

    /**
     * 不考虑线程安全，仅仅控制日志输出
     */
    private int i = 0;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 调用拒绝元数据
     */
    private ConcurrentHashMap<String, ServiceRejectMeta> callerRejectMeta = new ConcurrentHashMap<>();

    /**
     * 无凭证调用标识：true-任意调用，false-凭证调用
     */
    private boolean noCallerKeyNoInvoke;

    /**
     * 调用者函数列表
     */
    private ConcurrentHashMap<String, Long> callerKeyFunction = new ConcurrentHashMap<>();

    public ServiceOrFunctionReject(String serviceName, boolean noCallerKeyNoInvoke) {
        this.serviceName = serviceName;
        this.noCallerKeyNoInvoke = noCallerKeyNoInvoke;
    }

    void cleanTimeLessThanLastMin(ConcurrentHashMap<String, Long> callerKeyFunction) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        long time = calendar.getTimeInMillis();

        callerKeyFunction.forEach((key, value) -> {
            if (value < time) {
                callerKeyFunction.remove(key);
            }
        });
    }

    /**
     * 限流过滤：是否限流
     */
    boolean isLimit(ServiceContext context, ServiceRejectMeta serviceRejectMeta) {
        if (context.getCallerKey() == null) {
            return false;
        }
        // 限流时间
        Long limitTime = callerKeyFunction.get(context.getCallerKey());
        if (limitTime != null) {
            // 对比限流时间和当前时间
            if (limitTime / (1000 * 60) == System.currentTimeMillis() / (1000 * 60)) {
                // 分钟数不相等
                return true;
            } else {
                // 过期限流指令删除
                callerKeyFunction.remove(context.getCallerKey());
            }
        }
        // 函数级限流
        if (serviceRejectMeta.getGranularity() != Constants.USAGE_GRANULARITY_FUNCTION) {
            return false;
        }
        String callerFunction = generateKey(context.getCallerKey(), context.getFunction());
        limitTime = callerKeyFunction.get(callerFunction);
        if (limitTime != null) {
            if (limitTime / (1000 * 60) == System.currentTimeMillis() / (1000 * 60)) {
                // 分钟数不相等
                return true;
            } else {
                callerKeyFunction.remove(callerFunction);
            }
        }
        return false;
    }

    private String generateKey(String callerKey, String function) {
        return new StringBuilder().append(callerKey).append("#").append(function).toString();
    }


    public Optional<ExecutableResult> executable(ServiceContext param) {
        try {
            if (noCallerKeyNoInvoke && StringUtil.isBlank(param.getCallerKey())) {
                return Optional.of(new ExecutableResult().setExecutable(false).setReason(RejectionReason.REJECT));
            }
            if (StringUtil.isBlank(param.getCallerKey())) {
                return Optional.empty();
            }
            // 1、获取拒绝元数据
            ServiceRejectMeta serviceRejectMeta = this.callerRejectMeta.get(param.getCallerKey());
            // 2、解析元数据 进行过滤
            if (serviceRejectMeta != null) {
                return analyzeExecuteable(param, serviceRejectMeta);
            } else {
                synchronized (this) {
                    if (this.callerRejectMeta.get(param.getCallerKey()) == null) {
                        this.refreshCallerMethod(param.getCallerKey(), true);
                    }
                }
                serviceRejectMeta = this.callerRejectMeta.get(param.getCallerKey());
                if (serviceRejectMeta != null) {
                    return this.analyzeExecuteable(param, serviceRejectMeta);
                }
            }
        } catch (Throwable e) {
            if (i++ % 1000 == 0) {
                logger.error("[ARCH_SDK_error_executable] param is {}", param, e);
            }
        }
        return Optional.empty();
    }

    /**
     * 分析拒绝 元数据对象 进行过滤
     */
    private Optional<ExecutableResult> analyzeExecuteable(ServiceContext param, ServiceRejectMeta serviceRejectMeta) {
        // 1、权限过滤
        // 服务端设置拒绝该调用方
        if (serviceRejectMeta.isReject()) {
            return Optional.of(new ExecutableResult().setExecutable(false).setReason(RejectionReason.REJECT));
        }
        // 没有调用关系、没有调用方、没有服务方、apiserver出错
        if (serviceRejectMeta.isNoCallerUsage()) {
            return Optional.empty();
        }

        // 如果是函数粒度，且服务方没有申请该函数
        if (serviceRejectMeta.getGranularity() == Constants.USAGE_GRANULARITY_FUNCTION && !serviceRejectMeta.getNotRejectFunction().contains(lowerImplClass(param.getFunction()))) {
            return Optional.of(new ExecutableResult().setExecutable(false).setReason(RejectionReason.REJECT));
        }
        // 2、限流过滤
        if (isLimit(param, serviceRejectMeta)) {
            return Optional.of(new ExecutableResult().setExecutable(false).setReason(RejectionReason.LIMIT));
        }
        return Optional.empty();
    }


    public void refreshCallerMethod(String callerkey) {
        refreshCallerMethod(callerkey, false);
    }

    /**
     * 刷新调用者方法
     */
    private void refreshCallerMethod(String callerkey, boolean exceptionAddDefault) {
        try {
            String json = HttpUtil.get(Constants.SERVER_MANAGE_URL + "/config/getServiceRejectMeta?callerKey=" + URLEncoder.encode(callerkey, "utf8") + "&serviceName=" + serviceName);
            ServiceResult<ServiceRejectMeta> serviceRejectMetaServiceResult = JsonHelper.fromJson(json, new TypeToken<ServiceResult<ServiceRejectMeta>>() {
            }.getType());

            processServiceRejectMeta(callerkey, exceptionAddDefault, serviceRejectMetaServiceResult);
            logger.info("[ARCH_SDK_end_refreshCallerMethod]serviceName={},callerkey={},serviceRejectMetaServiceResult={}", serviceName, callerkey, serviceRejectMetaServiceResult);
        } catch (Exception e) {
            logger.info("[ARCH_SDK_ignore_refreshCallerMethod]callerkey={},exceptionAddDefault={},errorMsg={}", callerkey, exceptionAddDefault, e.getMessage());
            if (exceptionAddDefault) {
                callerRejectMeta.put(callerkey, new ServiceRejectMeta().setCallerKey(callerkey).setReject(false).setNoCallerUsage(true));
            }
        }
    }

    /**
     * for mock test
     */
    public void processServiceRejectMeta(String callerkey, boolean exceptionAddDefault, ServiceResult<ServiceRejectMeta> serviceRejectMetaServiceResult) {
        if (serviceRejectMetaServiceResult.getStatus().equals(ServiceResult.SUCCESS)) {
            // 统一替换为小写，兼容count 别名问题
            ServiceRejectMeta result = serviceRejectMetaServiceResult.getResult();
            result.setNotRejectFunction(result.getNotRejectFunction().stream().map(this::lowerImplClass).collect(Collectors.toSet()));
            callerRejectMeta.put(callerkey, result);
        } else if (exceptionAddDefault) {
            callerRejectMeta.put(callerkey, new ServiceRejectMeta().setCallerKey(callerkey).setReject(false).setNoCallerUsage(true));
        }
    }


    public void limit(String caller, String function, long time) {
        if (StringUtil.isBlank(caller)) {
            return;
        }
        if (StringUtil.isBlank(function)) {
            // 调用者限流
            this.callerKeyFunction.put(caller, time);
        } else {
            // 调用函数限流
            this.callerKeyFunction.put(generateKey(caller, function), time);
        }
    }


    public void start() {
        ThreadPool.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> cleanTimeLessThanLastMin(this.callerKeyFunction), 5, 5, TimeUnit.MINUTES);
        ThreadPool.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> this.callerRejectMeta.keySet().forEach(this::refreshCallerMethod), 0, 1, TimeUnit.HOURS);
    }

    public String lowerImplClass(String implClassFunction) {
        int dotIndex = implClassFunction.indexOf('.');
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append(implClassFunction.substring(0, dotIndex).toLowerCase()).append(implClassFunction.substring(dotIndex)).toString();
    }


}
