package com.bin.webmonitor.model.dto;

import com.bin.webmonitor.component.StrJson;

public class FunctionTimeoutDto extends StrJson {

    private String functionName;
    private int functionTimout;
    private int originFunctionTimeout;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getFunctionTimout() {
        return functionTimout;
    }

    public void setFunctionTimout(int functionTimout) {
        this.functionTimout = functionTimout;
    }

    public int getOriginFunctionTimeout() {
        return originFunctionTimeout;
    }

    public void setOriginFunctionTimeout(int originFunctionTimeout) {
        this.originFunctionTimeout = originFunctionTimeout;
    }
}
