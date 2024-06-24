package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

/**
 * 服务端收集接口调用量
 */
public class ServiceFunctionKey extends BaseCollectorKey {

    private String serviceName;

    private String functionName;

    private FluxType fluxType;

    @Override
    public BaseCollectorKey gen(CollectorKey dto) {
        return new ServiceFunctionKey(dto.getServiceName(), dto.getFunctionName(),
            dto.getFluxType());
    }

    public ServiceFunctionKey() {
    }

    public ServiceFunctionKey(String serviceName, String functionName, FluxType fluxType) {
        this.serviceName = serviceName;
        this.functionName = functionName;
        this.fluxType = fluxType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceFunctionKey setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getFunctionName() {
        return functionName;
    }

    public ServiceFunctionKey setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    @Override
    public String toKey() {
        return new StringBuilder().append(this.getServiceName())
            .append("-").append(this.getFunctionName())
            .append("-").append(fluxType.getValue()).toString();
    }

    @Override
    public String desc() {
        return serviceName + "服务" + functionName + "函数" + fluxType.getValue();
    }

    @Override
    public String timeCostdesc() {
        return serviceName + "服务" + functionName + "函数" + fluxType.getMainValue() + "耗时";
    }

    @Override
    public String timeRealCostdesc() {
        return "服务函数执行耗时：" + serviceName;
    }

    @Override
    public String toString() {
        return "ServiceFunctionKey{" +
            "serviceName='" + serviceName + '\'' +
            ", functionName='" + functionName + '\'' +
            ", fluxType=" + fluxType +
            '}';
    }
}
