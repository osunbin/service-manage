package com.bin.webmonitor.repository.domain;

import com.bin.webmonitor.component.StrJson;

import java.util.Date;

/**
 *  服务方法对象
 */
public class ServiceFunction extends StrJson {
    
    private int id;
    
    /**
     * 服务方id
     */
    private int sid;
    
    /**
     * 接口名称
     */
    private String interfaceName;
    
    /**
     * 函数名名称
     */
    private String fname;
    
    /**
     * 实现类名称
     *
     */
    private String lookup;
    
    /**
     * 带泛型的函数签名
     */
    private String genericMethodSignature;
    
    /**
     * 不带泛型的函数签名
     */
    private String generalMethodSignature;
    
    /**
     * 多实现的默认标识
     */
    private boolean multiImplDefault;
    
    /**
     * IP
     */
    private String ip;
    
    /**
     * 函数分组id
     */
    private int sfgid;
    
    private Date createTime;
    
    private Date updateTime;
    
    public ServiceFunction() {
    }
    
    public ServiceFunction(int sid, String fname, String ip) {
        this.sid = sid;
        this.fname = fname;
        this.ip = ip;
    }
    
    public ServiceFunction(int sid, String interfaceName, String lookup, String fname, String genericMethodSignature, String generalMethodSignature, String ip) {
        this.sid = sid;
        this.interfaceName = interfaceName;
        this.lookup = lookup;
        this.fname = fname;
        this.genericMethodSignature = genericMethodSignature;
        this.generalMethodSignature = generalMethodSignature;
        this.ip = ip;
    }
    
    public int getId() {
        return id;
    }
    
    public ServiceFunction setId(int id) {
        this.id = id;
        return this;
    }
    
    public int getSid() {
        return sid;
    }
    
    public ServiceFunction setSid(int sid) {
        this.sid = sid;
        return this;
    }
    
    public String getInterfaceName() {
        return interfaceName;
    }
    
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    public String getFname() {
        return fname;
    }
    
    public void setFname(String fname) {
        this.fname = fname;
    }
    
    public String getLookup() {
        return lookup;
    }
    
    public void setLookup(String lookup) {
        this.lookup = lookup;
    }
    
    public String getGenericMethodSignature() {
        return genericMethodSignature;
    }
    
    public ServiceFunction setGenericMethodSignature(String genericMethodSignature) {
        this.genericMethodSignature = genericMethodSignature;
        return this;
    }
    
    public String getGeneralMethodSignature() {
        return generalMethodSignature;
    }
    
    public void setGeneralMethodSignature(String generalMethodSignature) {
        this.generalMethodSignature = generalMethodSignature;
    }
    
    public boolean isMultiImplDefault() {
        return multiImplDefault;
    }
    
    public void setMultiImplDefault(boolean multiImplDefault) {
        this.multiImplDefault = multiImplDefault;
    }
    
    public String getIp() {
        return ip;
    }
    
    public ServiceFunction setIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public ServiceFunction setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public ServiceFunction setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    public int getSfgid() {
        return sfgid;
    }
    
    public ServiceFunction setSfgid(int sfgid) {
        this.sfgid = sfgid;
        return this;
    }
}
