package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.component.StrJson;

public class ServiceNodeConfigVo extends StrJson {

    private int sid;
    private String ip;

    private Short newWeight; // 权重，null表示不更新。
    private Short originWeight;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Short getNewWeight() {
        return newWeight;
    }

    public void setNewWeight(Short newWeight) {
        this.newWeight = newWeight;
    }

    public Short getOriginWeight() {
        return originWeight;
    }

    public void setOriginWeight(Short originWeight) {
        this.originWeight = originWeight;
    }
}
