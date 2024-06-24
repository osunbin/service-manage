package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  熔断监控数据
 */
public class CircuitBreakMonitor {
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
     * 成功数
     */
    private int successCount;

    /**
     * 失败数
     */
    private int failCount;

    /**
     * 超时数
     */
    private int timeoutCount;

    /**
     * 熔断器状态。0：关闭；1：打开。
     */
    private int status;

    /**
     * 上报时间分钟数的毫秒时间
     */
    private long minTime;

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

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return this.failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getTimeoutCount() {
        return this.timeoutCount;
    }

    public void setTimeoutCount(int timeoutCount) {
        this.timeoutCount = timeoutCount;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getMinTime() {
        return this.minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
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
        return "CircuitBreakMonitorPo{" + "id=" + id + ", cid=" + cid + ", sid=" + sid + ", fid=" + fid + ", ip='" + ip + '\'' + ", successCount=" + successCount + ", failCount=" + failCount + ", timeoutCount=" + timeoutCount + ", status=" + status + ", minTime=" + minTime + ", createTime="
                + createTime + ", updateTime=" + updateTime + '}';
    }
}
