package com.bin.webmonitor.model;

import com.bin.webmonitor.component.StrJson;

import java.util.HashSet;
import java.util.Set;

public class ServiceRejectMeta extends StrJson {
    /**
     * 调用者密钥
     */
    private String callerKey;
    
    /**
     * 粒度：0-函数 1-服务
     */
    private int granularity;
    
    /**
     * 是否拒绝
     */
    private boolean reject;
    
    /**
     * 是否调用
     */
    private boolean noCallerUsage;
    
    /**
     * 不拒绝函数列表
     */
    private Set<String> notRejectFunction = new HashSet<>();
    
    public String getCallerKey() {
        return callerKey;
    }
    
    public ServiceRejectMeta setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }
    
    public int getGranularity() {
        return granularity;
    }
    
    public ServiceRejectMeta setGranularity(int granularity) {
        this.granularity = granularity;
        return this;
    }
    
    public boolean isNoCallerUsage() {
        return noCallerUsage;
    }
    
    public ServiceRejectMeta setNoCallerUsage(boolean noCallerUsage) {
        this.noCallerUsage = noCallerUsage;
        return this;
    }
    
    public boolean isReject() {
        return reject;
    }
    
    public ServiceRejectMeta setReject(boolean reject) {
        this.reject = reject;
        return this;
    }
    
    public Set<String> getNotRejectFunction() {
        return notRejectFunction;
    }
    
    public ServiceRejectMeta setNotRejectFunction(Set<String> notRejectFunction) {
        this.notRejectFunction = notRejectFunction;
        return this;
    }
    
}
