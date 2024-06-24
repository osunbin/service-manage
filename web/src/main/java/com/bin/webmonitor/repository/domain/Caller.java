package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  服务调用者
 */
public class Caller {
    
    private int id;
    
    /**
     * 调用者名称
     */
    private String callerName;
    
    /**
     * 调用者秘钥
     */
    private String callerKey;
    
    /**
     * 调用者说明
     */
    private String description;
    

    
    /**
     * 调用者的归属人
     */
    private String owners;
    
    private Date createTime;
    
    private Date updateTime;
    

    
    public int getId() {
        return id;
    }
    
    public Caller setId(int id) {
        this.id = id;
        return this;
    }
    
    public String getCallerName() {
        return callerName;
    }
    
    public Caller setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }
    
    public String getCallerKey() {
        return callerKey;
    }
    
    public Caller setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Caller setDescription(String description) {
        this.description = description;
        return this;
    }
    

    
    public String getOwners() {
        return owners;
    }
    
    public Caller setOwners(String owners) {
        this.owners = owners;
        return this;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public Caller setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public Caller setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    

    
    @Override
    public String toString() {
        return "Caller [id=" + id + ", callerName=" + callerName + ", callerKey=" + callerKey + ", description=" + description +  ", owners=" + owners + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }
    
}
