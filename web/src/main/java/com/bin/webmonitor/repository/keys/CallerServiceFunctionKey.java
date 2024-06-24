package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;
/**
 * 调用方调用服务函数的调用量计数
 */
public class CallerServiceFunctionKey extends BaseCollectorKey implements FluxKey {

    /**
     * 这个key是调用者分配的密钥
     */
    private String callerKey;

    private String serviceName;

    private String functionName;

    private FluxType fluxType;

    private String callerName;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        CallerServiceFunctionKey key = new CallerServiceFunctionKey(dto.getCallerKey(),
                dto.getServiceName(), dto.getFunctionName(), dto.getFluxType());
        key.setCallerName(dto.getCallerName());
        return key;
    }

    public CallerServiceFunctionKey() {
    }

    public CallerServiceFunctionKey(String callerKey, String serviceName, String functionName,
                                    FluxType fluxType) {
        this.callerKey = callerKey;
        this.serviceName = serviceName;
        this.functionName = functionName;
        this.fluxType = fluxType;
    }

    public String getCallerKey() {
        return callerKey;
    }

    public CallerServiceFunctionKey setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public CallerServiceFunctionKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getFunctionName() {
        return functionName;
    }

    public CallerServiceFunctionKey setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(this.getCallerKey()).append("-").append(
                this.getServiceName()).append("-").append(functionName).append(fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return (callerName == null ? "" : callerName) + "调用" + serviceName + "服务" + functionName + "函数的" + fluxType.getValue();
    }

    public CallerServiceFunctionKey setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }

    @Override
    public String timeCostdesc() {
        return (callerName == null ? "" : callerName) + "调用" + serviceName + "服务" + functionName + "函数的" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return "";
    }

    @Override
    public String toString() {
        return "CallerServiceFunctionKey [callerKey=" + callerKey + ", serviceName=" +
                serviceName + ", functionName="
                + functionName + "]";
    }
}
