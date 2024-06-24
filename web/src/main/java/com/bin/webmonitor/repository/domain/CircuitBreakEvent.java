package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  熔断事件记录
 */
public class CircuitBreakEvent {

    private int id;

    /**
     * 调用方 id
     */
    private int cid;

    /**
     * 服务方 id
     */
    private int sid;

    /**
     * 函数 id
     */
    private int fid;

    /**
     * 调用方IP
     */
    private String ip;

    /**
     * 事件类型。0：熔断关闭；1：熔断打开；2：client重新启动。
     */
    private int eventType;

    /**
     * 事件发生时间(毫秒)
     */
    private long eventTime;

    /**
     * 事件原因
     */
    private String reason;

    private Date createTime;

    private Date updateTime;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return this.cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getSid() {
        return this.sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getFid() {
        return this.fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getEventType() {
        return this.eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getEventTime() {
        return this.eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "CircuitBreakEventPo{" + "id=" + id + ", cid=" + cid + ", sid=" + sid + ", fid=" + fid + ", ip='" + ip + '\'' + ", eventType=" + eventType + ", eventTime=" + eventTime + ", reason='" + reason + '\'' + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
