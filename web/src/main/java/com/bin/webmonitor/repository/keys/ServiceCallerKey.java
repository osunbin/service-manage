package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

/**
 *  服务方统计不同调用方的调用量计数
 */
public class ServiceCallerKey extends BaseCollectorKey {

    private FluxType fluxType;

    private String serviceName;

    private String callerKey;

    private String callerName;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        return new ServiceCallerKey(dto.getFluxType(), dto.getServiceName(), dto.getCallerKey(),
                dto.getCallerName());
    }

    public ServiceCallerKey() {
    }

    public ServiceCallerKey(String serviceName, String callerKey, FluxType fluxType) {
        this.fluxType = fluxType;
        this.serviceName = serviceName;
        this.callerKey = callerKey;
    }

    public ServiceCallerKey(FluxType fluxType, String serviceName, String callerKey,
                            String callerName) {
        this.fluxType = fluxType;
        this.serviceName = serviceName;
        this.callerKey = callerKey;
        this.callerName = callerName;
    }

    public ServiceCallerKey setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceCallerKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getCallerKey() {
        return callerKey;
    }

    public ServiceCallerKey setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(this.getServiceName())
                .append("-")
                .append(this.getCallerKey())
                .append("-")
                .append(fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return callerName + "调用" + serviceName + "服务的" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return callerName + "调用" + serviceName + "服务的" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return this.timeCostdesc().replace("耗时", "执行耗时");
    }

    @Override
    public String toString() {
        return "ServiceCallerKey{" +
                "fluxType=" + fluxType +
                ", serviceName='" + serviceName + '\'' +
                ", callerKey='" + callerKey + '\'' +
                ", callerName='" + callerName + '\'' +
                '}';
    }

}
