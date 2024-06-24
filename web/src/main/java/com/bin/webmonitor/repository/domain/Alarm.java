package com.bin.webmonitor.repository.domain;

import java.util.Date;

public class Alarm {
    private Long id;

    /**
     * 客户端id
     */
    private Integer cid;

    /**
     * 服务id
     */
    private Integer sid;

    /**
     * 报警类型
     */
    private Byte alertType;

    /**
     * 报警人
     */
    private String owner;

    /**
     * 通知类型
     */
    private Byte notifyType;

    private Integer intervalMinutes;

    /**
     * 函数名
     */
    private String svcFunctions;

    /**
     * 流量类型
     */
    private Byte fluxType;

    /**
     * 调用者列表
     */
    private String callerIds;

    /**
     * 服务ip列表
     */
    private String svcIps;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Byte getAlertType() {
        return alertType;
    }

    public void setAlertType(Byte alertType) {
        this.alertType = alertType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Byte getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Byte notifyType) {
        this.notifyType = notifyType;
    }

    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public String getSvcFunctions() {
        return svcFunctions;
    }

    public void setSvcFunctions(String svcFunctions) {
        this.svcFunctions = svcFunctions;
    }

    public Byte getFluxType() {
        return fluxType;
    }

    public void setFluxType(Byte fluxType) {
        this.fluxType = fluxType;
    }

    public String getCallerIds() {
        return callerIds;
    }

    public void setCallerIds(String callerIds) {
        this.callerIds = callerIds;
    }

    public String getSvcIps() {
        return svcIps;
    }

    public void setSvcIps(String svcIps) {
        this.svcIps = svcIps;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AlarmPo{" + "cid=" + cid + ", sid=" + sid + ", alertType=" + alertType + ", owner='" + owner + '\'' + ", svcFunctions='" + svcFunctions + '\'' + ", fluxType=" + fluxType + ", callerIds='" + callerIds + '\'' + ", svcIps='" + svcIps + '\'' + '}';
    }
}
