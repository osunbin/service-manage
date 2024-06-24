package com.bin.client;

import com.bin.client.limiter.FluxControlReport;
import com.bin.client.limiter.ServiceOrFunctionReject;
import com.bin.client.model.ExecutableResult;
import com.bin.client.model.ServiceContext;
import com.bin.collector.request.ServiceFunctions;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.MethodKey;
import com.bin.webmonitor.common.util.HttpUtil;
import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.common.util.ThreadPool;
import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.model.ServiceCountMeta;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.bin.webmonitor.common.Constants.SERVER_MANAGE_URL;


public class ServerService {

    private static Logger logger = LoggerFactory.getLogger(ServerService.class);


    private ServiceOrFunctionReject serviceOrFunctionReject;

    private String serviceName;

    public static volatile boolean useManagerOnly = false;

    /**
     * 服务id集合
     */
    public static ConcurrentHashMap<String, Integer> functionIdMap = new ConcurrentHashMap<>();

    /**
     * 调用者清单
     */
    static ConcurrentHashMap<String, Integer> callerKeyIdMap = new ConcurrentHashMap<>();

    /**
     * 服务端统计元数据
     */
    private ServiceCountMeta serviceCountMeta = new ServiceCountMeta();

    public static ServerService instance;

    public synchronized static ServerService createInstance(String serviceName, boolean noCallerKeyNoInvoke) {
        if (instance == null) {
            instance = new ServerService(serviceName, noCallerKeyNoInvoke);
        }

        return instance;
    }

    private ServerService(String serviceName, boolean noCallerKeyNoInvoke) {
        this.serviceOrFunctionReject = new ServiceOrFunctionReject(serviceName, noCallerKeyNoInvoke);
        this.serviceName = serviceName;
    }

    public void start() {
        refreshCallerkeyIdMap();
        this.serviceOrFunctionReject.start();
        ThreadPool.EXECUTORS.execute(() -> {
            callerKeyIdMap.forEach((k, v) -> {
                this.serviceOrFunctionReject.refreshCallerMethod(k);
            });
        });
        CommandListener.getInstance();
        ThreadPool.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(this::refreshCallerkeyIdMap, 3L, TimeUnit.HOURS.toMinutes(1L), TimeUnit.MINUTES);

    }



    public String getServiceName() {
        return serviceName;
    }



    public void addCount(String callerKey, String methodSignature) {
        addCount(ServiceContext.of(callerKey,methodSignature));
    }

    public void addCount(ServiceContext serviceContext) {
        Integer functionId = functionIdMap.get(serviceContext.getFunction());
        if (functionId == null) {
            logger.debug("[ARCH_SDK_no_function_id]ServiceContext={}", serviceContext);
            return;
        }
        Integer callerKeyId;
        if (StringUtil.isBlank(serviceContext.getCallerKey())) {
            callerKeyId = -1;
        } else {
            callerKeyId = callerKeyIdMap.get(serviceContext.getCallerKey());
            if (callerKeyId == null) {
                logger.debug("[ARCH_SDK_no_callerKey]ServiceContext={}", serviceContext);
                callerKeyId = -1;
            }
        }
        FluxControlReport.getInstance().report(functionId, callerKeyId);


    }

    public Optional<ExecutableResult> executable(ServiceContext param) {
        return this.serviceOrFunctionReject.executable(param);
    }


    public void refreshCallerMethod(String callerkey) {
        this.serviceOrFunctionReject.refreshCallerMethod(callerkey);
    }


    public void limit(String caller, String function, long time) {
        this.serviceOrFunctionReject.limit(caller, function, time);
    }

    public void refreshCallerkeyIdMap() {
        ServiceResult<ServiceCountMeta> callerCountMetaServiceResult;
        String json = "";
        try {
            json = HttpUtil.get(SERVER_MANAGE_URL + "/config/getServiceCountMeta?serviceName=" + serviceName);

            callerCountMetaServiceResult = JsonHelper.fromJson(json, new TypeToken<ServiceResult<ServiceCountMeta>>() {
            }.getType());

            if (callerCountMetaServiceResult.getStatus().equals(ServiceResult.SUCCESS)) {
                callerKeyIdMap = callerCountMetaServiceResult.getResult().getCallerKeyIdMap();
                ConcurrentHashMap<String, Integer> tmp = callerCountMetaServiceResult.getResult().getFunctionIdMap();
                tmp.forEach((k, v) -> functionIdMap.put(clean(k).toLowerCase(), v));

                serviceCountMeta = callerCountMetaServiceResult.getResult();
            }

            logger.info("[ARCH_SDK_end_refreshCallerkeyIdMap]json={},callerKeyIdMap={},functionIdMap={},serviceCountMeta={}", json, callerKeyIdMap, functionIdMap, serviceCountMeta);
        } catch (Exception e) {
            logger.info("[ARCH_SDK_ignore_refreshCallerkeyIdMap]serviceName={},errorMsg={}", serviceName, e.getMessage());
        }
    }


    public void uploadFunction(Set<String> functions) {
        try {
            ServiceFunctions serviceFunctionsRequest = new ServiceFunctions();
            serviceFunctionsRequest.setServiceName(serviceName);
            serviceFunctionsRequest.setFunctionNames(functions);


            String json = HttpUtil.post(SERVER_MANAGE_URL + "/config/persistServiceFunctions", JsonHelper.toJson(serviceFunctionsRequest));

            logger.info("[ARCH_SDK_end_uploadFunction]serviceName={},functions={},json={}", serviceName, functions, json);
        } catch (Exception e) {
            logger.info("[ARCH_SDK_ignore_uploadFunction]serviceName={},functions={},errorMsg={}", serviceName, functions, e.getMessage());
        }
    }

    /**
     *  上传 那些 方法 需要处理
     */
    public void uploadMethodKey(Set<MethodKey> functions) {
        try {
            ServiceFunctions serviceFunctionsRequest = new ServiceFunctions();
            serviceFunctionsRequest.setServiceName(serviceName);
            serviceFunctionsRequest.setFunctionSignatures(functions);

            String json = HttpUtil.post(SERVER_MANAGE_URL + "/config/persistServiceFuncSignature", JsonHelper.toJson(serviceFunctionsRequest));

            logger.info("[ARCH_SDK_end_uploadFunctionSignature]serviceName={},functions={},json={}", serviceName, functions, json);
        } catch (Exception e) {
            logger.info("[ARCH_SDK_ignore_uploadFunctionSignature]serviceName={},functions={},errorMsg={}", serviceName, functions, e.getMessage());
        }
    }

    /**
     *  上传 服务端配置
     */
    public void uploadConfigs(String config, String log4j2) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                String res = null;
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("serviceName", serviceName);
                    params.put("config", Objects.isNull(config) ? "" : config);
                    params.put("log4j2", Objects.isNull(log4j2) ? "" : log4j2);

                    res = HttpUtil.post(Constants.SERVER_MANAGE_URL + "/config/uploadServerConfigs", params);
                    logger.info("[ARCH_SDK_end_uploadServerConfigs]res={}", res);
                } catch (Throwable e) {
                    logger.info("[ARCH_SDK_ignore_uploadServerConfigs]errMsg={}", e.getMessage());
                }
            }
        };

        ThreadPool.EXECUTORS.execute(runnable);
    }

    /**
     *  上传 server 一些元信息、类名、...
     */
    public void uploadServiceExt(String ext) {
        ThreadPool.EXECUTORS.execute(() -> {
            String res;
            String url = Constants.SERVER_MANAGE_URL + "/config/uploadServiceExt";
            try {
                Map<String, String> params = new HashMap<>();
                params.put("ext", ext);
                params.put("serviceName", serviceName);

                res = HttpUtil.post(url, params);
                logger.info("[ARCH_SDK_end_uploadServiceExt]res={}", res);
            } catch (Throwable e) {
                logger.error("[ERROR_ARCH_SDK_uploadServiceExt]url={}", url, e);
            }
        });
    }

    private String generateKey(String function, String callerKey, byte flag) {
        return function + "#" + callerKey + "#" + flag;
    }




    private static String clean(String methodKey) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = methodKey.length();
        int leftAngleBrackets = 0;
        for (int i = 0; i < length; i++) {
            char c = methodKey.charAt(i);
            if (c == '<') {
                leftAngleBrackets++;
            }
            if (leftAngleBrackets == 0) {
                if (i > 0 && i < length - 1) {
                    char leftChar = methodKey.charAt(i - 1);
                    char rightChar = methodKey.charAt(i + 1);
                    // 字符左侧为参数分隔符'(' or ','
                    boolean leftCharIsParameterSeparator = leftChar == '(' || leftChar == ',';
                    // 字符右侧为参数分隔符')' or ','
                    boolean rightCharIsParameterSeparator = rightChar == ')' || rightChar == ',';
                    // 字符右侧为数组标识"[]"
                    boolean rightCharsAreArray = i < length - 2 && rightChar == '[' && methodKey.charAt(i + 2) == ']';
                    if (leftCharIsParameterSeparator && (rightCharIsParameterSeparator || rightCharsAreArray)) {
                        stringBuilder.append("Object");
                    } else {
                        stringBuilder.append(c);
                    }
                } else {
                    stringBuilder.append(c);
                }
            }
            if (c == '>') {
                leftAngleBrackets--;
            }
        }
        return stringBuilder.toString();
    }
}
