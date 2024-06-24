package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

/**
 * 服务端收集调用方调用函数的用量计数
 */
public class ServiceFunctionCallerKey extends BaseCollectorKey {

    private String serviceName;

    private String callerKey;

    private String functionName;

    private FluxType fluxType;

    private String callerName;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        ServiceFunctionCallerKey key = new ServiceFunctionCallerKey(dto.getServiceName(),
            dto.getCallerKey(), dto.getFunctionName(), dto.getFluxType());
        return key.setCallerName(dto.getCallerName());
    }

    public ServiceFunctionCallerKey() {
    }

    public ServiceFunctionCallerKey(String serviceName, String callerKey, String functionName,
        FluxType fluxType) {
        this.serviceName = serviceName;
        this.callerKey = callerKey;
        this.functionName = functionName;
        this.fluxType = fluxType;
    }

    public ServiceFunctionCallerKey setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceFunctionCallerKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getCallerKey() {
        return callerKey;
    }

    public ServiceFunctionCallerKey setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }

    public String getFunctionName() {
        return functionName;
    }

    public ServiceFunctionCallerKey setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(this.getServiceName())
            .append("-").append(this.getFunctionName())
            .append("-").append(this.getCallerKey())
            .append("-").append(fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return callerName + "调用" + serviceName + "服务" + functionName + "函数" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return callerName + "调用" + serviceName + "服务" + functionName + "函数" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return null;
    }

    @Override
    public String toString() {
        return "ServiceFunctionCallerKey{" +
            "serviceName='" + serviceName + '\'' +
            ", callerKey='" + callerKey + '\'' +
            ", functionName='" + functionName + '\'' +
            ", fluxType=" + fluxType +
            ", callerName='" + callerName + '\'' +
            '}';
    }
}
