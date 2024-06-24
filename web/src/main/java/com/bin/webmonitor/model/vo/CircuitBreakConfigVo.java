package com.bin.webmonitor.model.vo;

import java.util.Date;

public class CircuitBreakConfigVo {
    private int cid;// 调用方 id
    private int sid;// 服务方 id
    private int fid;// 函数 id

    private boolean enabled;// 是否开启熔断机制。0：不是；1：是。
    private boolean forceOpened;// 是否强制开启熔断机制。0：不是；1：是。
    private boolean forceClosed;// 是否强制关闭熔断机制。0：不是；1：是。

   // @Min(value = 10, message = "滑动窗口大小在10~60s之间")
   // @Max(value = 60, message = "滑动窗口大小在10~60s之间")
    private int slideWindowInSeconds;// 滑动窗口时长（秒)

   // @Min(value = 1, message = "值应该大于0")
    private int requestVolumeThreshold;// 请求是否达到开启的阀值

   // @Min(value = 0, message = "错误比例在0~100之间")
   // @Max(value = 100, message = "错误比例在0~100之间")
    private int errorThresholdPercentage;// 错误比例，百分比，取值范围为[0,100]

  //  @Min(value = 1000, message = "休眠时间在1~600s之间")
  //  @Max(value = 600000, message = "休眠时间在1~600s之间")
    private int sleepWindowInMilliseconds;// 熔断时间窗口时长（毫秒)

    private Date createTime;// 创建时间
    private Date updateTime;// 修改时间

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
        return "CircuitBreakConfigVo{" + "cid=" + cid + ", sid=" + sid + ", fid=" + fid + ", enabled=" + enabled
            + ", forceOpened=" + forceOpened + ", forceClosed=" + forceClosed + ", slideWindowInSeconds="
            + slideWindowInSeconds + ", requestVolumeThreshold=" + requestVolumeThreshold
            + ", errorThresholdPercentage=" + errorThresholdPercentage + ", sleepWindowInMilliseconds="
            + sleepWindowInMilliseconds + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
