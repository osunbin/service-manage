package com.bin.webmonitor.naming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServiceCluster implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 服务名
     */
    private String serviceName;
    
    /**
     * 服务节点ip列表
     */
    private List<String> ips = new ArrayList<>();
    
    public ServiceCluster() {
    }
    
    public ServiceCluster(String serviceName, List<String> ips) {
        this.serviceName = serviceName;
        this.ips = ips;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public ServiceCluster setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public List<String> getIps() {
        return ips;
    }
    
    public ServiceCluster setIps(List<String> ips) {
        this.ips = ips;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceCluster{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", ips=").append(ips);
        sb.append('}');
        return sb.toString();
    }
}
