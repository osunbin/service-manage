package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

public class GatherServiceKey extends BaseCollectorKey {
    private FluxType fluxType;

    private String serviceName;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        return new ServiceKey(dto.getServiceName(), dto.getFluxType());
    }

    public GatherServiceKey() {
    }

    public GatherServiceKey(String serviceName, FluxType fluxType) {
        this.serviceName = serviceName;
        this.fluxType = fluxType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public GatherServiceKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    @Override
    public String toKey() {
        return this.getServiceName() + "-" + fluxType.getValue() + "-gather";
    }

    @Override
    public String desc() {
        return serviceName + "服务的所有调用方" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return serviceName + "服务的所有调用方" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return serviceName + "服务执行耗时";
    }

    @Override
    public String toString() {
        return "GatherServiceKey{" +
                "fluxType=" + fluxType +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
