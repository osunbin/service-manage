package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

/**
 *  调用方调用特定服务的调用量计数
 */
public class CallerServiceKey extends BaseCollectorKey {

    /**
     * 这个key是调用者分配的密钥
     */
    private String callerKey;

    private String serviceName;

    private FluxType fluxType;

    private String callerName;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        CallerServiceKey callerServiceKey = new CallerServiceKey(
            dto.getCallerKey(), dto.getServiceName(), dto.getFluxType());
        callerServiceKey.setCallerName(dto.getCallerName());
        return callerServiceKey;
    }

    public CallerServiceKey() {
    }

    public CallerServiceKey(String callerKey, String serviceName, FluxType fluxType) {
        this.callerKey = callerKey;
        this.serviceName = serviceName;
        this.fluxType = fluxType;
    }

    public String getCallerKey() {
        return callerKey;
    }

    public CallerServiceKey setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public CallerServiceKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(this.getCallerKey()).append("-").append(
            this.getServiceName()).append(fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return (callerName == null ? "" : callerName) + "调用" + serviceName + "服务的" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return (callerName == null ? "" : callerName) + "调用" + serviceName + "服务的" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return "";
    }

    @Override
    public String toString() {
        return "CallerServiceKey{" +
            "callerKey='" + callerKey + '\'' +
            ", serviceName='" + serviceName + '\'' +
            ", fluxType=" + fluxType +
            ", callerName='" + callerName + '\'' +
            '}';
    }

    public BaseCollectorKey setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }
}
