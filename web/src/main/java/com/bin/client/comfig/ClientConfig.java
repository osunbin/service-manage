package com.bin.client.comfig;

import com.bin.client.circuitbreak.CircuitBreakConfigChangeListener;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.HttpUtil;
import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.ThreadPool;
import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.naming.model.Status;
import com.bin.webmonitor.naming.model.Statuses;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta;
import com.bin.webmonitor.model.CallerDegradeMeta;
import com.bin.webmonitor.model.ServerNode;
import com.bin.webmonitor.model.ServerConfig;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientConfig {
    private static Logger logger = LoggerFactory.getLogger(ClientConfig.class);

    /**
     * 调用者密钥
     */
    private String callerKey;

    /**
     * 服务降级元数据
     */
    private ConcurrentHashMap<String, CallerDegradeMeta> serviceDegradeMeta = new ConcurrentHashMap<>();

    /**
     * 本地缓存的熔断配置信息
     */
    private ConcurrentMap<String, ConcurrentMap<String, CircuitBreakConfigMeta>> serviceMethodCircuitBreakConfigMap = new ConcurrentHashMap<String, ConcurrentMap<String, CircuitBreakConfigMeta>>();

    /**
     * 服务配置变化监听
     */
    private ConfigChangeListener configChangeListener;

    /**
     * 熔断配置变化监听
     */
    private CircuitBreakConfigChangeListener circuitBreakConfigChangeListener;

    /**
     * 上次触发更新配置
     */
    private ConcurrentMap<String, ServiceResult<ServerConfig>> preRefreshConfigs = new ConcurrentHashMap<>();


    public ClientConfig(String callerKey) {
        if (null == callerKey || callerKey.trim().equals("")) {
            throw new RuntimeException("caller key 不能为空");
        }
        this.callerKey = callerKey;
        ConfigService.init(callerKey);
        this.refreshCircuitBreakConfig(null, null); // 先刷新熔断配置信息

        AtomicInteger refreshCount = new AtomicInteger(0);
        ThreadPool.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> {
            logger.info("[ARCH_SDK_start_refresh_config_task]refreshCount={}", refreshCount.incrementAndGet());

            serviceDegradeMeta.keySet().forEach(this::refreshDegradeConfig);
            serviceDegradeMeta.keySet().forEach(this::refreshConfig);
            this.aysncUploadUsageConfigs(); // 上传调用方当前的调用关系配置信息

            this.refreshCircuitBreakConfig(null, null); // 刷新熔断配置信息
        }, 1, 1, TimeUnit.HOURS);
    }


    public ServiceResult<ServerConfig> getConfig(String serviceName) {
        logger.info("[ARCH_SDK_start_getConfig]serviceName={}", serviceName);
        // 1.获取远程最新配置
        ServiceResult<ServerConfig> remoteConfig = getRemoteConfig(serviceName, -1);

        if (Objects.nonNull(remoteConfig)) {
            if (remoteConfig.isSuccess()) { // 如果结果正常，直接返回
                logger.info("[ARCH_SDK_return_remote_config]serviceName={},remoteConfig={}", serviceName, remoteConfig);
                return remoteConfig;
            }

            if (remoteConfig.getCode() == Statuses.SERVICE_NO_NODES || remoteConfig.getCode() == Statuses.SERVICE_NO_NODES_AFTER_CONTROL_FILTER) {
                logger.info("[ARCH_SDK_SERVICE_NO_NODES]serviceName={},remoteConfig={}", serviceName, remoteConfig);
                return new ServiceResult<>(new Status(remoteConfig.getCode(), "服务端没有可用节点"));
            }
        }

        // 2.远程获取失败，则使用本地备份配置
        ServiceResult<ServerConfig> localConfig = ConfigService.loadServiceConfig(serviceName);
        if (Objects.nonNull(localConfig)) {
            logger.info("[ARCH_SDK_return_local_config]serviceName={},localConfig={}", serviceName, localConfig);
            return localConfig;
        }
        logger.info("[ARCH_SDK_return_exception_config]serviceName={},remoteConfig={},localConfig={}", serviceName, remoteConfig, localConfig);
        return new ServiceResult<>(new Status(Statuses.IO_EXCEPTION, "获取配置异常"));
    }

    /**
     * 刷新服务配置，用于定时任务
     *
     * @param serviceName 服务名称
     */
    public void refreshConfig(String serviceName) {
        logger.info("[ARCH_SDK_start_refreshConfig]serviceName={}", serviceName);

        ServiceResult<ServerConfig> remoteConfig = getRemoteConfig(serviceName, -1);
        if (Objects.isNull(remoteConfig) || !remoteConfig.isSuccess()) {
            logger.info("[ARCH_SDK_illegal_remote_config]serviceName={},remoteConfig={}", serviceName, remoteConfig);
            return;
        }

        ServiceResult<ServerConfig> preRefreshConfig = preRefreshConfigs.get(serviceName);
        if (Objects.equals(remoteConfig, preRefreshConfig)) {
            logger.info("[ARCH_SDK_no_config_change]serviceName={},remoteConfig={},preRefreshConfig={}", serviceName, remoteConfig, preRefreshConfig);
            return;
        }

        preRefreshConfigs.put(serviceName, remoteConfig);
        // 1.保存到本地
        ConfigService.saveServiceConfig(serviceName, remoteConfig);

        // 2.发送配置变更通知
        if (Objects.nonNull(this.configChangeListener)) {
            logger.info("[ARCH_SDK_send_config_change]serviceName={},remoteConfig={},localConfig={}", serviceName, remoteConfig);
            this.configChangeListener.onConfigChange(serviceName, remoteConfig.getResult());
        }

        // 3.上传调用方当前的调用关系配置信息
        this.aysncUploadUsageConfigs();
    }

    /**
     * 获取远程服务配置
     *
     * @param serviceName
     * @param clientTime
     * @return
     */
    public ServiceResult<ServerConfig> getRemoteConfig(String serviceName, long clientTime) {
        String json = null;
        try {
            json = HttpUtil.get(Constants.SERVER_MANAGE_URL + "/config/getConfig?callerKey=" + URLEncoder.encode(callerKey, "utf8") + "&serviceName=" + serviceName + "&clientConfigTime=" + clientTime);
            ServiceResult<ServerConfig> res = JsonHelper.fromJson(json, new TypeToken<ServiceResult<ServerConfig>>() {
            }.getType());

            logger.info("[ARCH_SDK_end_getRemoteConfig]serviceName={},clientTime={},res={}", serviceName, clientTime, res);
            return res;
        } catch (Throwable e) {
            logger.info("[ARCH_SDK_ignore_getRemoteConfig]serviceName={},clientTime={},errMsg={}", serviceName, clientTime, e.getMessage());
        }

        return null;
    }

    public ServiceResult<List<ServerNode>> getCloudNodes(String serviceName) {
        try {
            String url = Constants.SERVER_MANAGE_URL + "/config/getCloudNodes?callerKey=" + URLEncoder.encode(callerKey, "utf8") + "&serviceName=" + serviceName;
            String json = HttpUtil.get(url);

            ServiceResult<List<ServerNode>> serviceResult = JsonHelper.fromJson(json, new TypeToken<ServiceResult<List<ServerNode>>>() {
            }.getType());

            logger.info("[ARCH_SDK_end_getCloudNodes]serviceName={},url={},json={},serviceResult={}", serviceName, url, json, serviceResult);
            return serviceResult;
        } catch (Exception e) {
            logger.info("[ARCH_SDK_ignore_getCloudNodes]serviceName={},errorMsg={}", serviceName, e.getMessage());
        }

        return new ServiceResult<List<ServerNode>>(new Status(Statuses.IO_EXCEPTION, "获取使用新平台error")).setResult(null);
    }


    public boolean isDegrade(String service, String method) {
        CallerDegradeMeta callerDegradeMeta = serviceDegradeMeta.get(service);
        if (callerDegradeMeta != null) {
            if (callerDegradeMeta.isDegrade()) {
                return true;
            } else {
                return callerDegradeMeta.getDegradeFunctions().contains(method);
            }
        } else {
            synchronized (this) {
                if (serviceDegradeMeta.get(service) == null) {
                    this.refreshDegradeConfig(service, true);
                }
            }
            callerDegradeMeta = serviceDegradeMeta.get(service);
            if (callerDegradeMeta.isDegrade()) {
                return true;
            } else {
                return callerDegradeMeta.getDegradeFunctions().contains(method);
            }
        }
    }


    public void refreshDegradeConfig(String service) {
        refreshDegradeConfig(service, false);
    }


    public CircuitBreakConfigMeta getCircuitBreakConfig(String service, String method) {
        ConcurrentMap<String, CircuitBreakConfigMeta> method2CircuitBreakConfigMap = serviceMethodCircuitBreakConfigMap.get(service);
        if (Objects.isNull(method2CircuitBreakConfigMap)) {
            return null;
        }
        return method2CircuitBreakConfigMap.get(method);
    }


    public void refreshCircuitBreakConfig( String service,  String method) {
        try {
            // 1.构建URL
            String url = Constants.SERVER_MANAGE_URL + "/config/getCallerCircuitBreakConfigMeta?callerKey=" + URLEncoder.encode(callerKey, "utf8");
            if (Objects.nonNull(service)) {
                url += "&serviceName=" + URLEncoder.encode(service, "utf8");
            }
            if (Objects.nonNull(method)) {
                url += "&method=" + URLEncoder.encode(method, "utf8");
            }

            // 2.http获取结果并反序列化
            String json = HttpUtil.get(url);
            ServiceResult<CallerCircuitBreakConfigMeta> callerCircuitBreakConfigMetaServiceResult = JsonHelper.fromJson(json, new TypeToken<ServiceResult<CallerCircuitBreakConfigMeta>>() {
            }.getType());

            // 如果刷新的是全量配置，先清空本地缓存
            if (Objects.isNull(service) && Objects.isNull(method)) {
                this.serviceMethodCircuitBreakConfigMap.clear();
            }

            // 3.将最新的结果整合到本地缓存中
            if (callerCircuitBreakConfigMetaServiceResult.getStatus().equals(ServiceResult.SUCCESS)) {
                Map<String, Map<String, CircuitBreakConfigMeta>> newServiceMethodConfigMap = callerCircuitBreakConfigMetaServiceResult.getResult().getServiceMethodConfigMap();

                newServiceMethodConfigMap.entrySet().stream().forEach(entry -> {
                    String tmpService = entry.getKey();
                    ConcurrentMap<String, CircuitBreakConfigMeta> localMethod2ConfigMap = serviceMethodCircuitBreakConfigMap.get(tmpService);
                    if (Objects.isNull(localMethod2ConfigMap)) {
                        localMethod2ConfigMap = new ConcurrentHashMap<String, CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta>();
                        serviceMethodCircuitBreakConfigMap.put(tmpService, localMethod2ConfigMap);
                    }

                    localMethod2ConfigMap.putAll(entry.getValue()); // 放进本地缓存
                });

            }

            logger.info("[ARCH_SDK_end_refreshCircuitBreakConfig]service={},method={},url={},json={},callerCircuitBreakConfigMetaServiceResult={}", service, method, url, json, callerCircuitBreakConfigMetaServiceResult);
        } catch (Exception e) {
            logger.info("[ARCH_SDK_ignore_refreshCircuitBreakConfig]service={},method={},errorMsg={}", service, method, e.getMessage());
        }

    }


    public void registerConfigChangeListener(ConfigChangeListener configChangeListener) {
        this.configChangeListener = configChangeListener;
    }


    public ConfigChangeListener getConfigChangeListener() {
        return this.configChangeListener;
    }


    public void registerCircuitBreakConfigChangeListener(CircuitBreakConfigChangeListener circuitBreakConfigChangeListener) {
        this.circuitBreakConfigChangeListener = circuitBreakConfigChangeListener;
    }


    public CircuitBreakConfigChangeListener getCircuitBreakConfigChangeListener() {
        return circuitBreakConfigChangeListener;
    }

    private void refreshDegradeConfig(String service, boolean exceptionAddDefault) {
        try {
            String url = Constants.SERVER_MANAGE_URL + "/config/getCallerDegradeMeta?callerKey=" + URLEncoder.encode(callerKey, "utf8") + "&serviceName=" + service;
            String json = HttpUtil.get(url);
            ServiceResult<CallerDegradeMeta> callerDegradeMetaServiceResult = JsonHelper.fromJson(json, new TypeToken<ServiceResult<CallerDegradeMeta>>() {
            }.getType());
            if (callerDegradeMetaServiceResult.getStatus().equals(ServiceResult.SUCCESS)) {
                serviceDegradeMeta.put(service, callerDegradeMetaServiceResult.getResult());
            } else {
                if (exceptionAddDefault) {
                    serviceDegradeMeta.put(service, new CallerDegradeMeta().setService(service).setDegrade(false));
                }
            }

            logger.info("[ARCH_SDK_end_refreshDegradeConfig]service={},exceptionAddDefault={},url={},json={},callerDegradeMetaServiceResult={}", service, exceptionAddDefault, url, json, callerDegradeMetaServiceResult);
        } catch (Exception e) {
            if (exceptionAddDefault) {
                serviceDegradeMeta.put(service, new CallerDegradeMeta().setService(service).setDegrade(false));// in
                // case
            }
            logger.warn("[ARCH_SDK_ignore_refreshDegradeConfig]service={},errorMsg={}", service, e.getMessage());
        }
    }

    /**
     * 异步上传所有调用关系的配置信息，方便排查问题
     */
    public void aysncUploadUsageConfigs() {
        Runnable runnable = new Runnable() {

            public void run() {
                String res = null;
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("callerKey", URLEncoder.encode(callerKey, "utf8"));

                    String usageConfigs = JsonHelper.toJson(preRefreshConfigs.values());
                    params.put("usageConfigs", usageConfigs);

                    res = HttpUtil.post(Constants.SERVER_MANAGE_URL + "/config/uploadClientUsageConfigs", params);
                    logger.info("[ARCH_SDK_end_uploadClientUsageConfigs]res={}", res);
                } catch (Throwable e) {
                    logger.info("[ARCH_SDK_ignore_uploadClientUsageConfigs]errMsg={}", e.getMessage());
                }
            }
        };

        ThreadPool.EXECUTORS.execute(runnable);
    }
}
