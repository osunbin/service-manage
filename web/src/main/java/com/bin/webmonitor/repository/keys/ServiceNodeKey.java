package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

/**
 * 服务端某个节点的调用量
 */
public class ServiceNodeKey extends BaseCollectorKey {

    private String serviceName;

    private String serviceIp;

    private FluxType fluxType;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        return new ServiceNodeKey(dto.getServiceName(), dto.getServiceIp(), dto.getFluxType());
    }

    public ServiceNodeKey() {
    }

    public ServiceNodeKey(String serviceName, String serviceIp, FluxType fluxType) {
        this.serviceName = serviceName;
        this.serviceIp = serviceIp;
        this.fluxType = fluxType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceNodeKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public ServiceNodeKey setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
        return this;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(this.getServiceName())
            .append("-").append(this.getServiceIp()).append("-").append(
                fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return serviceIp + "节点" + serviceName + "服务" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return serviceIp + "节点" + serviceName + "服务" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return "服务节点执行耗时：" + serviceName + "-" + serviceIp;
    }

    @Override
    public String toString() {
        return "ServiceNodeKey{" +
            "serviceName='" + serviceName + '\'' +
            ", serviceIp='" + serviceIp + '\'' +
            ", fluxType=" + fluxType +
            '}';
    }

}
