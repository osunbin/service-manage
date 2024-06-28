package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.component.StrJson;
import com.bin.webmonitor.naming.ServiceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;

public class ServiceNodeVO extends StrJson implements Comparable<ServiceNodeVO> {
    
    private final Logger logger = LoggerFactory.getLogger(ServiceNodeVO.class);
    
    private int sid;
    
    private String ip;
    
    private boolean update;
    
    private boolean runing;
    
    private boolean on;
    
    private String version;
    
    private int tcpPort;
    
    private Short weight;
    
    private String serverType = "未知";
    
    private String onlineTime = "";
    
    private String updateTime = "";
    
    private int pid;
    
    private String idcName;
    
    private String systemEnv;
    
    public ServiceNodeVO from(ServiceNode node) {
        this.runing = node.isRunning();
        this.ip = node.getIp();
        this.on = node.isInService();
        
        this.version = node.getContainerVersion();
        this.tcpPort = node.getPort();
        this.weight = node.getWeight();
        this.pid = node.getPid();
        this.systemEnv = node.getSystemEnvType();
        
        switch (node.getServerType()) {
            case "VM":
                this.serverType = "虚拟机";
                break;
            case "SERVER":
                this.serverType = "物理机";
                break;
            case "DOCKER":
                this.serverType = "Docker";
                break;
            default:
                this.serverType = "Docker";
                break;
        }
        
        try {

            Date updateDate = TimeUtil.allTimeStr2Date("" + node.getUpdateTime());
            Date onLineDate = TimeUtil.allTimeStr2Date("" + node.getOnLineTime());
            this.updateTime = TimeUtil.date2fullStr(updateDate);
            this.onlineTime = TimeUtil.date2fullStr(onLineDate);
        } catch (Exception e) {
            logger.error("[ERROR-from] node.getUpdateTime()={},node.getOnLineTime()={}", node.getUpdateTime(), node.getOnLineTime(), e);
        }
        
        return this;
        
    }
    
    public int getSid() {
        return sid;
    }
    
    public void setSid(int sid) {
        this.sid = sid;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public boolean isUpdate() {
        return update;
    }
    
    public void setUpdate(boolean update) {
        this.update = update;
    }
    
    public boolean isRuning() {
        return runing;
    }
    
    public void setRuning(boolean runing) {
        this.runing = runing;
    }
    
    public boolean isOn() {
        return on;
    }
    
    public void setOn(boolean on) {
        this.on = on;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getTcpPort() {
        return tcpPort;
    }
    
    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }
    
    public Short getWeight() {
        return weight;
    }
    
    public void setWeight(Short weight) {
        this.weight = weight;
    }
    
    public String getServerType() {
        return serverType;
    }
    
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
    
    public String getOnlineTime() {
        return onlineTime;
    }
    
    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }
    
    public String getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    public int getPid() {
        return pid;
    }
    
    public void setPid(int pid) {
        this.pid = pid;
    }
    
    public String getIdcName() {
        return idcName;
    }
    
    public void setIdcName(String idcName) {
        this.idcName = idcName;
    }
    
    public String getSystemEnv() {
        return systemEnv;
    }
    
    public void setSystemEnv(String systemEnv) {
        this.systemEnv = systemEnv;
    }
    
    @Override
    public int compareTo(ServiceNodeVO another) {
        // 1.运行状态
        if (!Objects.equals(this.runing, another.runing)) {
            return this.runing ? -1 : 1;
        }
        
        // 2.机器类型
        if (!Objects.equals(this.serverType, another.serverType)) {
            return this.serverType.compareTo(another.serverType);
        }
        
        // 3.ip
        return this.ip.compareTo(another.ip);
    }
}
