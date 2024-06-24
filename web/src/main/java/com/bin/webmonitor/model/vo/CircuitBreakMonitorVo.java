package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.common.util.TimeUtil;

import java.util.Date;

public class CircuitBreakMonitorVo {
    private int id;// 自增id
    private int cid;// 调用方 id
    private int sid;// 服务方 id
    private int fid;// 函数 id
    private String ip;// 调用方IP

    private int successCount;// 成功数
    private int failCount;// 失败数
    private int timeoutCount;// 超时数
    private int status;// 熔断器状态。0：关闭；1：打开。
    private long minTime;// 上报时间分钟数的毫秒时间

    private Date createTime;// 创建时间
    private Date updateTime;// 修改时间

    private String service;// 服务方
    private String method;// 接口名称
    private String minTimeStr;// minTime的前端展示
    private String statusStr;// 状态的前端展示

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

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getTimeoutCount() {
        return timeoutCount;
    }

    public void setTimeoutCount(int timeoutCount) {
        this.timeoutCount = timeoutCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;

        if (status == 0) {
            this.statusStr = "关闭";
        } else if (status == 1) {
            this.statusStr = "打开";
        } else {
            this.statusStr = "未知";
        }
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
        this.minTimeStr = TimeUtil.date2fullStr(new Date(minTime)).substring(0, 16);
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

    public String getMinTimeStr() {
        return minTimeStr;
    }

    public String getStatusStr() {
        return statusStr;
    }

    @Override
    public String toString() {
        return "CircuitBreakMonitorVo{" + "id=" + id + ", cid=" + cid + ", sid=" + sid + ", fid=" + fid + ", ip='" + ip
            + '\'' + ", successCount=" + successCount + ", failCount=" + failCount + ", timeoutCount=" + timeoutCount
            + ", status=" + status + ", minTime=" + minTime + ", createTime=" + createTime + ", updateTime="
            + updateTime + ", service='" + service + '\'' + ", method='" + method + '\'' + ", minTimeStr='" + minTimeStr
            + '\'' + ", statusStr='" + statusStr + '\'' + '}';
    }
}
