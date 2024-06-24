package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

public class ServiceNodeFunctionKey extends BaseCollectorKey {

    private String serviceName;

    private String serviceIp;

    private String functionName;

    private FluxType fluxType;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        return new ServiceNodeFunctionKey(dto.getServiceName(), dto.getServiceIp(),
            dto.getFunctionName(), dto.getFluxType());
    }

    public ServiceNodeFunctionKey() {
    }

    public ServiceNodeFunctionKey(String serviceName, String serviceIp, String functionName,
        FluxType fluxType) {
        this.serviceName = serviceName;
        this.fluxType = fluxType;
        this.serviceIp = serviceIp;
        this.functionName = functionName;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(serviceName).append("-").append(serviceIp)
            .append("-").append(functionName).append("-").append(fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return serviceIp + "节点" + serviceName + "服务" + functionName + "函数" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return serviceIp + "节点" + serviceName + "服务" + functionName + "函数" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return "节点函数执行耗时：" + serviceName + "-" + serviceIp + "-" + functionName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceNodeFunctionKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public ServiceNodeFunctionKey setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
        return this;
    }

    public String getFunctionName() {
        return functionName;
    }

    public ServiceNodeFunctionKey setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    @Override
    public String toString() {
        return "ServiceNodeFunctionKey{" +
            "serviceName='" + serviceName + '\'' +
            ", serviceIp='" + serviceIp + '\'' +
            ", functionName='" + functionName + '\'' +
            ", fluxType=" + fluxType +
            '}';
    }

}
