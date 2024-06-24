package com.bin.client.model;

import com.bin.webmonitor.enums.FluxType;

public class ServiceContext {
    /**
     * 调用者密钥
     */
    private String callerKey = "";

    /**
     * 函数名
     */
    private String function = "";

    /**
     * 流量类型
     */
    private FluxType flag = FluxType.Normal;

    /**
     * 服务端耗时 = 队列等待时间 + 业务执行时间
     */
    private int totalCost;

    /**
     * 业务执行时间
     */
    private int executeCost;

    public ServiceContext(String callerKey,String function) {
        this.callerKey = callerKey;
        this.function = function;
    }


    public ServiceContext(String function, String callerKey, FluxType flag, int totalCost, int executeCost) {
        this.callerKey = callerKey;
        this.function = function;
        this.flag = flag;
        this.totalCost = totalCost;
        this.executeCost = executeCost;
    }

    public static ServiceContext of(String callerKey,String function) {
        return new ServiceContext(callerKey,function);
    }

    public String getCallerKey() {
        return callerKey;
    }

    public ServiceContext setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }

    public String getFunction() {
        return function;
    }

    public ServiceContext setFunction(String function) {
        this.function = function;
        return this;
    }

    public FluxType getFlag() {
        return flag;
    }

    public ServiceContext setFlag(FluxType flag) {
        this.flag = flag;
        return this;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public ServiceContext setTotalCost(int totalCost) {
        this.totalCost = totalCost;
        return this;
    }

    public int getExecuteCost() {
        return executeCost;
    }

    public ServiceContext setExecuteCost(int executeCost) {
        this.executeCost = executeCost;
        return this;
    }

    @Override
    public String toString() {
        return "ServiceContext [callerKey=" + callerKey + ", function=" + function + ", flag=" + flag + ", totalCost=" + totalCost + ", executeCost=" + executeCost + "]";
    }

}
