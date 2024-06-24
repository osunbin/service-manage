package com.bin.webmonitor.repository.domain;

import java.util.Date;
import java.util.List;

/**
 *  服务分组对象：服务分组映射，可用于服务分级场景
 */
public class Group {
    
    private int id;
    
    /**
     * 分组所属的服务ID
     */
    
    private int sid;
    
    /**
     * 分组名称
     */
    
    private String groupName;
    
    /**
     * 分组状态
     */
    private int status;
    
    private Date createTime;
    
    private Date updateTime;
    
    private List<String> ips;
    
    public List<String> getIps() {
        return ips;
    }
    
    public Group setIps(List<String> ips) {
        this.ips = ips;
        return this;
    }
    
    public int getId() {
        return id;
    }
    
    public Group setId(int id) {
        this.id = id;
        return this;
    }
    
    public int getSid() {
        return sid;
    }
    
    public Group setSid(int sid) {
        this.sid = sid;
        return this;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public Group setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }
    
    public int getStatus() {
        return status;
    }
    
    public Group setStatus(int status) {
        this.status = status;
        return this;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public Group setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public Group setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    @Override
    public String toString() {
        return "Group [id=" + id + ", sid=" + sid + ", groupName=" + groupName + ", status=" + status + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }
    
}
