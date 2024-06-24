package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  服务节点分组信息
 */
public class ServiceNodeGroup {
    
    /**
     * 所属分组ID
     */
    private int gid;
    
    /**
     * 服务节点IP
     */
    private String ip;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    public int getGid() {
        return gid;
    }
    
    public ServiceNodeGroup setGid(int gid) {
        this.gid = gid;
        return this;
    }
    
    public String getIp() {
        return ip;
    }
    
    public ServiceNodeGroup setIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public ServiceNodeGroup setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public ServiceNodeGroup setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    @Override
    public String toString() {
        return "ServiceNodeGroup{" + "gid=" + gid + ", ip='" + ip + '\'' + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
}
