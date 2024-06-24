package com.bin.webmonitor.model;

import java.util.HashMap;
import java.util.Map;

public class CallerCountMeta {
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 服务id
     */
    private int serviceId;
    
    /**
     * 函数id Map
     */
    private Map<String, Integer> functionIdMap = new HashMap<>();
    
    public String getServiceName() {
        return serviceName;
    }
    
    public CallerCountMeta setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public int getServiceId() {
        return serviceId;
    }
    
    public CallerCountMeta setServiceId(int serviceId) {
        this.serviceId = serviceId;
        return this;
    }
    
    public Map<String, Integer> getFunctionIdMap() {
        return functionIdMap;
    }
    
    public CallerCountMeta setFunctionIdMap(Map<String, Integer> functionIdMap) {
        this.functionIdMap = functionIdMap;
        return this;
    }
    
    @Override
    public String toString() {
        return "CallerCountMeta{" + "serviceName='" + serviceName + '\'' + ", serviceId=" + serviceId + ", functionIdMap=" + functionIdMap + '}';
    }
}
