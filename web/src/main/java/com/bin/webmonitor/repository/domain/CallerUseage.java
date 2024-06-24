package com.bin.webmonitor.repository.domain;

import com.bin.webmonitor.component.StrJson;

import java.io.Serializable;
import java.util.Date;

/**
 *  调用者配置信息
 */
public class CallerUseage extends StrJson implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    /**
     * 调用者ID
     */
    private int cid;

    /**
     * 服务ID(冗余字段，为了根据服务查询调用方)
     */
    private int sid;

    /**
     * 服务分组ID
     */
    private int gid;

    /**
     * 本调用者对服务每分钟的调用限制次数
     */
    private int quantity;

    /**
     * 是否拒绝调用
     */
    private boolean reject;

    /**
     * 调用粒度。0函数级；1服务级。
     */
    private int granularity;

    /**
     * 使用描述
     */
    private String description;



    /**
     * 协议参数：序列化方式
     */
    private String protocolSerialize;



    /**
     * 协议参数：压缩类型
     */
    private String protocolCompressType;

    /**
     * 协议参数：协议类型/协议版本
     */
    private String protocolProtocolType;

    /**
     * 服务参数：判断失效等待时间
     */
    private String serverDeadTimeout;



    /**
     * 降级标识: 0-不降级 1-降级
     */
    private boolean degrade;

    private Date createTime;

    private Date updateTime;

    private boolean receiveCallerAlert = true;

    public int getCid() {
        return cid;
    }

    public CallerUseage setCid(int cid) {
        this.cid = cid;
        return this;
    }

    public int getSid() {
        return sid;
    }

    public CallerUseage setSid(int sid) {
        this.sid = sid;
        return this;
    }

    public int getGid() {
        return gid;
    }

    public CallerUseage setGid(int gid) {
        this.gid = gid;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public CallerUseage setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public boolean isReject() {
        return reject;
    }

    public CallerUseage setReject(boolean reject) {
        this.reject = reject;
        return this;
    }

    public int getGranularity() {
        return granularity;
    }

    public CallerUseage setGranularity(int granularity) {
        this.granularity = granularity;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CallerUseage setDescription(String description) {
        this.description = description;
        return this;
    }


    public String getProtocolSerialize() {
        return protocolSerialize;
    }

    public CallerUseage setProtocolSerialize(String protocolSerialize) {
        this.protocolSerialize = protocolSerialize;
        return this;
    }



    public String getProtocolCompressType() {
        return protocolCompressType;
    }

    public CallerUseage setProtocolCompressType(String protocolCompressType) {
        this.protocolCompressType = protocolCompressType;
        return this;
    }

    public String getProtocolProtocolType() {
        return protocolProtocolType;
    }

    public CallerUseage setProtocolProtocolType(String protocolProtocolType) {
        this.protocolProtocolType = protocolProtocolType;
        return this;
    }

    public String getServerDeadTimeout() {
        return serverDeadTimeout;
    }

    public CallerUseage setServerDeadTimeout(String serverDeadTimeout) {
        this.serverDeadTimeout = serverDeadTimeout;
        return this;
    }



    public boolean isDegrade() {
        return degrade;
    }

    public CallerUseage setDegrade(boolean degrade) {
        this.degrade = degrade;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public CallerUseage setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public CallerUseage setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public boolean isReceiveCallerAlert() {
        return receiveCallerAlert;
    }

    public CallerUseage setReceiveCallerAlert(boolean receiveCallerAlert) {
        this.receiveCallerAlert = receiveCallerAlert;
        return this;
    }

    public int getId() {
        return id;
    }

    public CallerUseage setId(int id) {
        this.id = id;
        return this;
    }

}
