package com.bin.webmonitor.repository.domain;

import java.io.Serializable;
import java.util.Date;

public class CallerFunctionUseage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 调用者ID
     */
    private Integer cid;

    /**
     * 服务ID(冗余字段，为了根据服务查询调用方)
     */
    private Integer sid;

    /**
     * 服务函数ID
     */
    private Integer fid;

    /**
     * 本调用者对服务每分钟的调用限制次数
     */
    private int quantity;

    /**
     * 函数调用的超时时间
     */
    private int timeout;

    /**
     * 函数名
     */
    private String fname;

    private Date updateTime;

    private Date createTime;

    /**
     * 降级标识
     */
    private boolean degrade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public CallerFunctionUseage setCid(Integer cid) {
        this.cid = cid;
        return this;
    }

    public Integer getSid() {
        return sid;
    }

    public CallerFunctionUseage setSid(Integer sid) {
        this.sid = sid;
        return this;
    }

    public Integer getFid() {
        return fid;
    }

    public CallerFunctionUseage setFid(Integer fid) {
        this.fid = fid;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public CallerFunctionUseage setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getFname() {
        return fname;
    }

    public CallerFunctionUseage setFname(String fname) {
        this.fname = fname;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public CallerFunctionUseage setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public CallerFunctionUseage setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public boolean isDegrade() {
        return degrade;
    }

    public CallerFunctionUseage setDegrade(boolean degrade) {
        this.degrade = degrade;
        return this;
    }
}

