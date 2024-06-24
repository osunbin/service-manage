package com.bin.webmonitor.naming;

import com.bin.webmonitor.common.util.JsonHelper;

import java.io.Serializable;

public class ServiceNode implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 服务id
     */
    private Long serviceId;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 集群名字
     */
    private String clusterName;
    
    /**
     * 服务器类型 unknown、vm-虚拟机、server-物理机、docker
     */
    private String serverType;
    
    /**
     * 环境类型 online-线上、gray-灰度、sandbox-沙箱、sandboxStable-沙箱稳定、sit-测试、uat-稳定测试
     */
    private String systemEnvType;
    
    /**
     * 容器类型：http、web、dubbo
     */
    private String containerType;
    
    /**
     * 版本号
     */
    private String containerVersion;
    
    /**
     * ip
     */
    private String ip;
    
    /**
    * 端口
    */
    private Integer port;
    

    private Integer pid;
    
    /**
     * 调用者标识
     */
    private String callerKey;
    
    /**
     * 运行状态 0:停止 1:运行中
     */
    private Boolean running;
    
    /**
     * 可用状态 0:停止 1:服务可以正确处理请求
     */
    private Boolean inService;
    
    /**
     * 上线时间：服务第一次发送心跳时间(晚于启动时间, 不等于服务可用时间)
     */
    private Long onLineTime;
    
    /**
     * 下线时间：服务与控制中心链接断开的时间
     */
    private Long offLineTime;
    
    /**
     * 心跳时间
     */
    private Long updateTime;
    
    /**
     * idc机房名字
     */
    private String idcName;
    
    /**
     * 分组信息
     */
    private String groupArray;
    
    /**
     * 权重
     */
    private Short weight;
    
    /**
     * 扩展属性
     */
    private String extJson;




    public String getIdcName() {
        return idcName;
    }
    
    public ServiceNode setIdcName(String idcName) {
        this.idcName = idcName;
        return this;
    }
    
    public String getGroupArray() {
        return groupArray;
    }
    
    public ServiceNode setGroupArray(String groupArray) {
        this.groupArray = groupArray;
        return this;
    }
    
    public Long getUpdateTime() {
        return updateTime;
    }
    
    public ServiceNode setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    public Long getServiceId() {
        return serviceId;
    }
    
    public ServiceNode setServiceId(Long serviceId) {
        this.serviceId = serviceId;
        return this;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public ServiceNode setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public String getClusterName() {
        return clusterName;
    }
    
    public ServiceNode setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }
    
    public String getServerType() {
        return serverType;
    }
    
    public ServiceNode setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }
    
    public String getContainerType() {
        return containerType;
    }
    
    public ServiceNode setContainerType(String containerType) {
        this.containerType = containerType;
        return this;
    }
    
    public String getSystemEnvType() {
        return systemEnvType;
    }
    
    public ServiceNode setSystemEnvType(String systemEnvType) {
        this.systemEnvType = systemEnvType;
        return this;
    }
    
    public String getIp() {
        return ip;
    }
    
    public ServiceNode setIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public ServiceNode setPort(Integer port) {
        this.port = port;
        return this;
    }
    
    public Integer getPid() {
        return pid;
    }
    
    public ServiceNode setPid(Integer pid) {
        this.pid = pid;
        return this;
    }
    
    public String getCallerKey() {
        return callerKey;
    }
    
    public ServiceNode setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }
    
    public Boolean isRunning() {
        return running;
    }
    
    public ServiceNode setRunning(Boolean running) {
        this.running = running;
        return this;
    }
    
    public Boolean isInService() {
        return inService;
    }
    
    public ServiceNode setInService(boolean inService) {
        this.inService = inService;
        return this;
    }
    
    public Long getOnLineTime() {
        return onLineTime;
    }
    
    public ServiceNode setOnLineTime(Long onLineTime) {
        this.onLineTime = onLineTime;
        return this;
    }
    
    public Long getOffLineTime() {
        return offLineTime;
    }
    
    public ServiceNode setOffLineTime(Long offLineTime) {
        this.offLineTime = offLineTime;
        return this;
    }
    
    public String getContainerVersion() {
        return containerVersion;
    }
    
    public ServiceNode setContainerVersion(String containerVersion) {
        this.containerVersion = containerVersion;
        return this;
    }
    
    public Short getWeight() {
        return weight;
    }
    
    public ServiceNode setWeight(Short weight) {
        this.weight = weight;
        return this;
    }
    
    @Override
    public String toString() {
        return JsonHelper.toJson(this);
    }
    
    public String getExtJson() {
        return extJson;
    }
    
    public ServiceNode setExtJson(String extJson) {
        this.extJson = extJson;
        return this;
    }
}
