package com.bin.webmonitor.repository.keys;

import com.bin.webmonitor.enums.FluxType;

public class CollectorKey {
    private String callerKey;

    private String serviceName;

    private FluxType fluxType;

    private String callerName;

    private String functionName;

    private String serviceIp;

    private String callerIp;

    public String getCallerKey() {
        return callerKey;
    }

    public void setCallerKey(String callerKey) {
        this.callerKey = callerKey;
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

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public String getCallerIp() {
        return callerIp;
    }

    public void setCallerIp(String callerIp) {
        this.callerIp = callerIp;
    }
}
