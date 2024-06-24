package com.bin.webmonitor.common;

import java.util.Objects;

public class MethodKey {

    /**
     * 实现类名
     */
    private String lookup;

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 是否多实现
     */
    private boolean multiImplDefault;

    /**
     * 带有泛型的函数签名
     */
    private String methodSignatureWithGenericTypes;

    /**
     * 不带泛型的函数签名
     */
    private String methodSignatureWithoutGenericTypes;

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public boolean isMultiImplDefault() {
        return multiImplDefault;
    }

    public void setMultiImplDefault(boolean multiImplDefault) {
        this.multiImplDefault = multiImplDefault;
    }

    public String getMethodSignatureWithGenericTypes() {
        return methodSignatureWithGenericTypes;
    }

    public void setMethodSignatureWithGenericTypes(String methodSignatureWithGenericTypes) {
        this.methodSignatureWithGenericTypes = methodSignatureWithGenericTypes;
    }

    public String getMethodSignatureWithoutGenericTypes() {
        return methodSignatureWithoutGenericTypes;
    }

    public void setMethodSignatureWithoutGenericTypes(String methodSignatureWithoutGenericTypes) {
        this.methodSignatureWithoutGenericTypes = methodSignatureWithoutGenericTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodKey that = (MethodKey)o;
        return multiImplDefault == that.multiImplDefault && Objects.equals(lookup, that.lookup) && Objects.equals(interfaceName, that.interfaceName) && Objects.equals(methodSignatureWithGenericTypes, that.methodSignatureWithGenericTypes)
                && Objects.equals(methodSignatureWithoutGenericTypes, that.methodSignatureWithoutGenericTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lookup, interfaceName, multiImplDefault, methodSignatureWithGenericTypes, methodSignatureWithoutGenericTypes);
    }

    @Override
    public String toString() {
        return "MethodKey{" + "lookup='" + lookup + '\'' + ", interfaceName='" + interfaceName + '\'' + ", multiImplDefault=" + multiImplDefault + ", methodSignatureWithGenericTypes='" + methodSignatureWithGenericTypes + '\'' + ", methodSignatureWithoutGenericTypes='"
                + methodSignatureWithoutGenericTypes + '\'' + '}';
    }
}
