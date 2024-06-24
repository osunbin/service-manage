package com.bin.webmonitor.model;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceCountMeta {
    /**
     * 调用者id集合
     */
    private ConcurrentHashMap<String, Integer> callerKeyIdMap = new ConcurrentHashMap<>();
    
    /**
     * 函数id集合
     */
    private ConcurrentHashMap<String, Integer> functionIdMap = new ConcurrentHashMap<>();
    
    /**
     * 调用基准值
     */
    private int costBase;
    
    /**
     * 调用者密钥跟名字映射
     */
    private ConcurrentHashMap<String, String> callerKeyCallerNameMap = new ConcurrentHashMap<>();
    
    public ConcurrentHashMap<String, Integer> getCallerKeyIdMap() {
        return callerKeyIdMap;
    }
    
    public ServiceCountMeta setCallerKeyIdMap(ConcurrentHashMap<String, Integer> callerKeyIdMap) {
        this.callerKeyIdMap = callerKeyIdMap;
        return this;
    }
    
    public ConcurrentHashMap<String, Integer> getFunctionIdMap() {
        return functionIdMap;
    }
    
    public ServiceCountMeta setFunctionIdMap(ConcurrentHashMap<String, Integer> functionIdMap) {
        this.functionIdMap = functionIdMap;
        return this;
    }
    
    public int getCostBase() {
        return costBase;
    }
    
    public ServiceCountMeta setCostBase(int costBase) {
        this.costBase = costBase;
        return this;
    }
    
    public ConcurrentHashMap<String, String> getCallerKeyCallerNameMap() {
        return callerKeyCallerNameMap;
    }
    
    public ServiceCountMeta setCallerKeyCallerNameMap(ConcurrentHashMap<String, String> callerKeyCallerNameMap) {
        this.callerKeyCallerNameMap = callerKeyCallerNameMap;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceCountMeta{");
        sb.append("callerKeyIdMap=").append(callerKeyIdMap);
        sb.append(", functionIdMap=").append(functionIdMap);
        sb.append(", costBase=").append(costBase);
        sb.append(", callerKeyCallerNameMap=").append(callerKeyCallerNameMap);
        sb.append('}');
        return sb.toString();
    }
}
