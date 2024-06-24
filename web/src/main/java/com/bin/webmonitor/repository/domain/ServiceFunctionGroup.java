package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  服务函数分组对象
 */
public class ServiceFunctionGroup {
    
    private int id;

    /**
     * 服务id
     */
    private int sid;

    /**
     * 分组名
     */
    private String groupName;
    
    private Date createTime;
    
    private Date updateTime;
    
    public int getId() {
        return id;
    }
    
    public ServiceFunctionGroup setId(int id) {
        this.id = id;
        return this;
    }
    
    public int getSid() {
        return sid;
    }
    
    public ServiceFunctionGroup setSid(int sid) {
        this.sid = sid;
        return this;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public ServiceFunctionGroup setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public ServiceFunctionGroup setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public ServiceFunctionGroup setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceFunctionGroup{");
        sb.append("id=").append(id);
        sb.append(", sid=").append(sid);
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
