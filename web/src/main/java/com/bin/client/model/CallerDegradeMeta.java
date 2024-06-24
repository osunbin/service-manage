package com.bin.client.model;

import java.util.HashSet;
import java.util.Set;

public class CallerDegradeMeta {
    
    /**
     * 服务名
     */
    private String service;
    
    /**
     * 是否降级
     */
    private boolean isDegrade;
    
    /**
     * 降级函数
     */
    private Set<String> degradeFunctions = new HashSet<>();
    
    public String getService() {
        return service;
    }
    
    public CallerDegradeMeta setService(String service) {
        this.service = service;
        return this;
    }
    
    public Set<String> getDegradeFunctions() {
        return degradeFunctions;
    }
    
    public CallerDegradeMeta setDegradeFunctions(Set<String> degradeFunctions) {
        this.degradeFunctions = degradeFunctions;
        return this;
    }
    
    public boolean isDegrade() {
        return isDegrade;
    }
    
    public CallerDegradeMeta setDegrade(boolean degrade) {
        this.isDegrade = degrade;
        return this;
    }
    
    public CallerDegradeMeta setIsDegrade(boolean degrade) {
        this.isDegrade = degrade;
        return this;
    }
    
    public boolean getIsDegrade() {
        return isDegrade;
    }
    
    @Override
    public String toString() {
        return "CallerDegradeMeta{" + "service='" + service + '\'' + ", isDegrade=" + isDegrade + ", degradeFunctions=" + degradeFunctions + '}';
    }
}
