package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

/**
 * 服务端收集全局调用量
 */
public class ServiceKey extends BaseCollectorKey {

    private FluxType fluxType;

    private String serviceName;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        return new ServiceKey(dto.getServiceName(), dto.getFluxType());
    }

    public ServiceKey() {
    }

    public ServiceKey(String serviceName, FluxType fluxType) {
        this.serviceName = serviceName;
        this.fluxType = fluxType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    @Override
    public String toKey() {
        return this.getServiceName() + "-" + fluxType.getValue();
    }

    @Override
    public String desc() {
        return serviceName + "服务" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return serviceName + "服务" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return "服务执行耗时：" + serviceName;
    }

    @Override
    public String toString() {
        return "ServiceKey{" +
            "fluxType=" + fluxType +
            ", serviceName='" + serviceName + '\'' +
            '}';
    }

}
