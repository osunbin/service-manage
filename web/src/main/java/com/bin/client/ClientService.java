package com.bin.client;

import com.bin.client.circuitbreak.CircuitBreakConfigChangeListener;
import com.bin.client.comfig.ClientConfig;
import com.bin.client.comfig.ConfigChangeListener;
import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta;
import com.bin.webmonitor.model.ServerNode;
import com.bin.webmonitor.model.ServerConfig;

import java.util.List;

public class ClientService {

    private ClientConfig clientConfig;

    public static ClientService instance;


    public static synchronized ClientService createInstance(String callerKey) {
        if (instance == null) {
            instance = new ClientService(callerKey);
        }
        return instance;
    }

    public ClientService(String callerKey) {
        this.clientConfig = new ClientConfig(callerKey);
    }

    /**
     *   根据service name返回配置信息
     *   会根据本地缓存配置
     * @param serviceName
     * @return
     */
    public ServiceResult<ServerConfig> getConfig(String serviceName) {
        return this.clientConfig.getConfig(serviceName);
    }

    /**
     *  刷新服务的配置信息
     * @param serviceName
     */
    public void refreshConfig(String serviceName) {
        this.clientConfig.refreshConfig(serviceName);
    }

    /**
     *  获取该服务云分组的结点
     * @param serviceName
     * @return
     */
    public ServiceResult<List<ServerNode>> getCloudNodes(String serviceName) {
        return this.clientConfig.getCloudNodes(serviceName);
    }

    /**
     *  是否降级
     * @param service
     * @param method
     * @return
     */
    public boolean isDegrade(String service, String method) {
        return this.clientConfig.isDegrade(service, method);
    }

    /**
     *  得到拉取降级配置时调用
     * @param service
     */
    public void refreshDegradeConfig(String service) {
        this.clientConfig.refreshDegradeConfig(service);
    }

    /**
     *  获取熔断配置
     * @param service
     * @param method
     * @return
     */
    public CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta getCircuitBreakConfig(String service, String method) {
        return this.clientConfig.getCircuitBreakConfig(service, method);
    }

    /**
     *  刷新熔断配置
     * @param service
     * @param method
     */
    public void refreshCircuitBreakConfig(String service, String method) {
        this.clientConfig.refreshCircuitBreakConfig(service, method);
    }

    /**
     *  配置变化调用
     */
    public void registerConfigChangeListener(ConfigChangeListener configChangeListener) {
        this.clientConfig.registerConfigChangeListener(configChangeListener);
    }

    /**
     *  服务治理配置变化监听
     * @return
     */
    public ConfigChangeListener getConfigChangeListener() {
        return this.clientConfig.getConfigChangeListener();
    }

    /**
     *  注册熔断配置变化的监听器
     * @param circuitBreakConfigChangeListener
     */
    public void registerCircuitBreakConfigChangeListener(CircuitBreakConfigChangeListener circuitBreakConfigChangeListener) {
        this.clientConfig.registerCircuitBreakConfigChangeListener(circuitBreakConfigChangeListener);
    }

    /**
     *  熔断配置变化监控器
     * @return
     */
    public CircuitBreakConfigChangeListener getCircuitBreakConfigChangeListener() {
        return this.clientConfig.getCircuitBreakConfigChangeListener();
    }


}
