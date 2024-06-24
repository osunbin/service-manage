package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  调用方配置对象
 */
public class ClientConfig {
    private int id;
    
    /**
     * 调用者id
     */
    private int cid;
    
    /**
     * 调用者ip
     */
    private String ip;
    
    /**
     * 调用者配置信息
     */
    private String usageConfig;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    private String updateTimeStr = "无";
    
    public ClientConfig() {
        this.createTime = new Date();
    }
    
    public ClientConfig(int cid, String ip, String usageConfig) {
        this();
        this.cid = cid;
        this.ip = ip;
        this.usageConfig = usageConfig;
        
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setCid(int cid) {
        this.cid = cid;
    }
    
    public int getCid() {
        return cid;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setUsageConfig(String usageConfig) {
        this.usageConfig = usageConfig;
    }
    
    public String getUsageConfig() {
        return usageConfig;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public String getUpdateTimeStr() {
        return updateTimeStr;
    }
    
    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientConfigMapper{");
        sb.append("id=").append(id);
        sb.append(", cid='").append(cid).append('\'');
        sb.append(", ip=").append(ip);
        sb.append(", usageConfig='").append(usageConfig).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
