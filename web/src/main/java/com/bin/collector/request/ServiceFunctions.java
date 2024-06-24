package com.bin.collector.request;

import com.bin.webmonitor.common.MethodKey;

import java.util.HashSet;
import java.util.Set;

public class ServiceFunctions extends BaseData {
    /**
     * 服务名
     */
    private String serviceName = "";

    /**
     * 函数名列表
     */
    private Set<String> functionNames = new HashSet<>();

    /**
     * 函数特性列表
     */
    private Set<MethodKey> functionSignatures = new HashSet<>();

    public ServiceFunctions() {
        this.reportType = reportType.serviceFunction;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Set<String> getFunctionNames() {
        return functionNames;
    }

    public void setFunctionNames(Set<String> functionNames) {
        this.functionNames = functionNames;
    }

    public ServiceFunctions addFunctionName(String functionName) {
        this.functionNames.add(functionName);
        return this;
    }

    public Set<MethodKey> getFunctionSignatures() {
        return functionSignatures;
    }

    public void setFunctionSignatures(Set<MethodKey> functionSignatures) {
        this.functionSignatures = functionSignatures;
    }

    public ServiceFunctions addFunctionSignature(MethodKey functionSignature) {
        this.functionSignatures.add(functionSignature);
        return this;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ServiceFunctionsRequest{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", functionNames=").append(functionNames);
        sb.append(", functionSignatures=").append(functionSignatures);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public ServiceFunctions toRequest(byte[] b) {
        return null;
    }
}
