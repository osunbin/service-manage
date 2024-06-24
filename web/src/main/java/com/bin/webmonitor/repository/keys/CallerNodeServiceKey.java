package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

public class CallerNodeServiceKey extends BaseCollectorKey {

    private String callerKey;

    private String callerIp;

    private String serviceName;

    private FluxType fluxType;

    private String callerName;

    public CallerNodeServiceKey(){}

    public CallerNodeServiceKey(String callerKey, String callerIp, String serviceName, FluxType fluxType) {
        this.callerKey = callerKey;
        this.callerIp = callerIp;
        this.serviceName = serviceName;
        this.fluxType = fluxType;
    }

    public String getCallerKey() {
        return callerKey;
    }

    public void setCallerKey(String callerKey) {
        this.callerKey = callerKey;
    }

    public String getCallerIp() {
        return callerIp;
    }

    public void setCallerIp(String callerIp) {
        this.callerIp = callerIp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public FluxType getFluxType() {
        return fluxType;
    }

    public void setFluxType(FluxType fluxType) {
        this.fluxType = fluxType;
    }

    public String getCallerName() {
        return callerName;
    }

    public CallerNodeServiceKey setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(this.callerKey).append("-").append(
                this.callerIp).append("-").append(this.serviceName).append(fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return new StringBuilder().append(this.callerName).append("的").append(this.callerIp).append(
                "节点调用").append(this.serviceName).append(this.fluxType.getValue()).toString();
    }

    @Override
    public String timeCostdesc() {
        return new StringBuilder().append(this.callerName).append("的").append(this.callerIp).append(
                "节点调用").append(this.serviceName).append(this.fluxType.getMainValue()).append("耗时").toString();
    }

    @Override
    public String timeRealCostdesc() {
        return new StringBuilder().append(this.callerName).append("的").append(this.callerIp).append(
                "节点调用").append(this.serviceName).append(this.fluxType.getMainValue()).append("耗时").toString();
    }

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        CallerNodeServiceKey key = new CallerNodeServiceKey(dto.getCallerKey(), dto.getCallerIp(),
                dto.getServiceName(), dto.getFluxType());
        key.setCallerName(dto.getCallerName());
        return key;
    }

    @Override
    public String toString() {
        return "CallerNodeServiceKey{" +
                "callerKey='" + callerKey + '\'' +
                ", callerIp='" + callerIp + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", fluxType=" + fluxType +
                ", callerName='" + callerName + '\'' +
                '}';
    }
}
