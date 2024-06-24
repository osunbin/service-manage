package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  熔断配置
 */
public class CircuitBreakConfig {

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
     * 是否开启熔断机制。0：不是；1：是。
     */
    private boolean enabled;

    /**
     * 是否强制开启熔断机制。0：不是；1：是。
     */
    private boolean forceOpened;

    /**
     * 是否强制关闭熔断机制。0：不是；1：是。
     */
    private boolean forceClosed;

    /**
     * 滑动窗口时长（秒)
     */
    private int slideWindowInSeconds;

    /**
     * 窗口内进行熔断判断的最小请求个数（请求数过少则不会启动熔断机制）
     */
    private int requestVolumeThreshold;

    /**
     * 错误比例，百分比，取值范围为[0,100]
     */
    private int errorThresholdPercentage;

    /**
     * 熔断时间窗口时长（毫秒)
     */
    private int sleepWindowInMilliseconds;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isForceOpened() {
        return forceOpened;
    }

    public void setForceOpened(boolean forceOpened) {
        this.forceOpened = forceOpened;
    }

    public boolean isForceClosed() {
        return forceClosed;
    }

    public void setForceClosed(boolean forceClosed) {
        this.forceClosed = forceClosed;
    }

    public int getSlideWindowInSeconds() {
        return slideWindowInSeconds;
    }

    public void setSlideWindowInSeconds(int slideWindowInSeconds) {
        this.slideWindowInSeconds = slideWindowInSeconds;
    }

    public int getRequestVolumeThreshold() {
        return this.requestVolumeThreshold;
    }

    public void setRequestVolumeThreshold(int requestVolumeThreshold) {
        this.requestVolumeThreshold = requestVolumeThreshold;
    }

    public int getErrorThresholdPercentage() {
        return this.errorThresholdPercentage;
    }

    public void setErrorThresholdPercentage(int errorThresholdPercentage) {
        this.errorThresholdPercentage = errorThresholdPercentage;
    }

    public int getSleepWindowInMilliseconds() {
        return this.sleepWindowInMilliseconds;
    }

    public void setSleepWindowInMilliseconds(int sleepWindowInMilliseconds) {
        this.sleepWindowInMilliseconds = sleepWindowInMilliseconds;
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
        return "CircuitBreakConfigPo{" + "id=" + id + ", cid=" + cid + ", sid=" + sid + ", fid=" + fid + ", enabled=" + enabled + ", forceOpened=" + forceOpened + ", forceClosed=" + forceClosed + ", slideWindowInSeconds=" + slideWindowInSeconds + ", requestVolumeThreshold=" + requestVolumeThreshold
                + ", errorThresholdPercentage=" + errorThresholdPercentage + ", sleepWindowInMilliseconds=" + sleepWindowInMilliseconds + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
