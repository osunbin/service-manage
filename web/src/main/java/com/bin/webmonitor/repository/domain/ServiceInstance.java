package com.bin.webmonitor.repository.domain;

import java.util.Date;
import java.util.List;

public class ServiceInstance {
    
    private int id;
    
    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务职能描述
     */
    private String description;
    
    /**
     * 负责人(归属人)
     */
    private String owners;
    
    /**
     * 服务的TCP端口
     */
    private int tcpPort;
    
    /**
     * telnet调试端口
     */
    private int telnetPort;
    
    private Date createTime;
    
    private Date updateTime;

    
    private boolean allowServiceGranularity;
    

    private String createTimeStr;
    




    

    private List<Group> groups;
    
    public List<Group> getGroups() {
        return groups;
    }
    
    public ServiceInstance setGroups(List<Group> groups) {
        this.groups = groups;
        return this;
    }
    


    

    public int getId() {
        return id;
    }
    
    public ServiceInstance setId(int id) {
        this.id = id;
        return this;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public ServiceInstance setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    

    
    public String getDescription() {
        return description;
    }
    
    public ServiceInstance setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public String getOwners() {
        return owners;
    }
    
    public ServiceInstance setOwners(String owners) {
        this.owners = owners;
        return this;
    }
    
    public int getTcpPort() {
        return tcpPort;
    }
    
    public ServiceInstance setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
        return this;
    }
    
    public int getTelnetPort() {
        return telnetPort;
    }
    
    public ServiceInstance setTelnetPort(int telnetPort) {
        this.telnetPort = telnetPort;
        return this;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public ServiceInstance setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public String getCreateTimeStr() {
        return createTimeStr;
    }
    
    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public ServiceInstance setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    

    
    public boolean isAllowServiceGranularity() {
        return allowServiceGranularity;
    }
    
    public void setAllowServiceGranularity(boolean allowServiceGranularity) {
        this.allowServiceGranularity = allowServiceGranularity;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Service{");
        sb.append("id=").append(id);
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", owners='").append(owners).append('\'');
        sb.append(", tcpPort=").append(tcpPort);
        sb.append(", telnetPort=").append(telnetPort);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", allowServiceGranularity=").append(allowServiceGranularity);
        sb.append('}');
        return sb.toString();
    }
}
