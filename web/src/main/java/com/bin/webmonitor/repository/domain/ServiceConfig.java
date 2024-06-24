package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  服务配置对象
 */
public class ServiceConfig {
    
    private int id;
    
    /**
     * 服务id
     */
    private int sid;
    
    /**
     * 服务ip
     */
    private String ip;
    
    /**
     * 服务基础配置信息
     */
    private String config;
    
    /**
     * 服务日志配置信息
     */
    private String log4j;
    
    /**
     * 服务额外信息
     */
    private String ext;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String updateTimeStr = "无";
    
    public ServiceConfig() {
        this.createTime = new Date();
    }
    
    public ServiceConfig(int sid, String ip, String config, String log4j) {
        this();
        this.sid = sid;
        this.ip = ip;
        this.config = config;
        this.log4j = log4j;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setSid(int sid) {
        this.sid = sid;
    }
    
    public int getSid() {
        return sid;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getIp() {
        return ip;
    }


    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setLog4j(String log4j) {
        this.log4j = log4j;
    }
    
    public String getLog4j() {
        return log4j;
    }
    
    public String getExt() {
        return ext;
    }
    
    public void setExt(String ext) {
        this.ext = ext;
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
        final StringBuilder sb = new StringBuilder("ServiceConfig{");
        sb.append("id=").append(id);
        sb.append(", sid='").append(sid).append('\'');
        sb.append(", ip=").append(ip);
        sb.append(", config='").append(config).append('\'');
        sb.append(", log4j='").append(log4j).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
