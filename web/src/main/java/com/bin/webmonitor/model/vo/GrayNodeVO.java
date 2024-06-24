package com.bin.webmonitor.model.vo;

public class GrayNodeVO {
    private String ip;
    private String caller;
    private String callerIps;

    public String getIp() {
        return ip;
    }

    public GrayNodeVO setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getCaller() {
        return caller;
    }

    public GrayNodeVO setCaller(String caller) {
        this.caller = caller;
        return this;
    }

    public String getCallerIps() {
        return callerIps;
    }

    public GrayNodeVO setCallerIps(String callerIps) {
        this.callerIps = callerIps;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GrayNodeVO{");
        sb.append("ip='").append(ip).append('\'');
        sb.append(", caller='").append(caller).append('\'');
        sb.append(", callerIps='").append(callerIps).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
