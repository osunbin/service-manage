package com.bin.webmonitor.naming;

public class ServiceNodeWeight {
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * ip
     */
    private String ip;
    
    /**
     * 权重
     */
    private Short weight;
    
    public ServiceNodeWeight() {
    }
    
    public ServiceNodeWeight(String serviceName, String ip, Short weight) {
        this.serviceName = serviceName;
        this.ip = ip;
        this.weight = weight;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public Short getWeight() {
        return weight;
    }
    
    public void setWeight(Short weight) {
        this.weight = weight;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    @Override
    public String toString() {
        return "ServiceNodeWeight [serviceName=" + serviceName + ", ip=" + ip + ", weight=" + weight + "]";
    }
    
}