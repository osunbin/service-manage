package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.enums.CircuitBreakEventType;

import java.util.Date;

public class CircuitBreakEventVo {
    private int id;// 自增id
    private int cid;// 调用方 id
    private int sid;// 服务方 id
    private int fid;// 函数 id
    private String ip;// 调用方IP

    private int eventType;// 事件类型。0：熔断关闭；1：熔断打开；2：client重新启动。
    private long eventTime;// 时件发生时的毫秒时间
    private String reason; // 事件原因

    private Date createTime;// 创建时间
    private Date updateTime;// 修改时间

    private String service;// 服务方
    private String method;// 接口名称
    private String eventTypeStr;// minTime的前端展示
    private String eventTimeStr;// minTime的前端展示

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getFid() {
        return fid;
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
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;

        this.eventTypeStr = CircuitBreakEventType.codeOf((byte)eventType).getDesc();
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
        this.eventTimeStr = TimeUtil.date2fullStr(new Date(eventTime));
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEventTypeStr() {
        return eventTypeStr;
    }

    public String getEventTimeStr() {
        return eventTimeStr;
    }

    @Override
    public String toString() {
        return "CircuitBreakEventVo{" + "id=" + id + ", cid=" + cid + ", sid=" + sid + ", fid=" + fid + ", ip='" + ip
            + '\'' + ", eventType=" + eventType + ", eventTime=" + eventTime + ", reason='" + reason + '\''
            + ", createTime=" + createTime + ", updateTime=" + updateTime + ", service='" + service + '\''
            + ", method='" + method + '\'' + ", eventTypeStr='" + eventTypeStr + '\'' + ", eventTimeStr='"
            + eventTimeStr + '\'' + '}';
    }
}
