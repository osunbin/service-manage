package com.bin.webmonitor.repository.domain;

import com.bin.webmonitor.component.StrJson;

import java.util.Date;

/**
 *  调用关系扩展信息表对应的model
 */
public class CallerUsageExt extends StrJson {


    public static final int CONFIG_TYPE_UNKNOWN = 0; // 配置来源：未知
    public static final int CONFIG_TYPE_SRVMGR = 1;// 配置来源：服务管理平台
    public static final int CONFIG_TYPE_LOCAL = 2;// 配置来源：本地配置

    private int id;// 自增id
    private int cid;// 调用方 id
    private int sid;// 服务方 id
    private Integer configType;// 配置来源。0：未知；1：服务管理平台；2：本地配置。
    private Date createTime;// 创建时间
    private Date updateTime;// 修改时间

    public int getId() {
        return id;
    }

    public CallerUsageExt setId(int id) {
        this.id = id;
        return this;
    }

    public int getCid() {
        return cid;
    }

    public CallerUsageExt setCid(int cid) {
        this.cid = cid;
        return this;
    }

    public int getSid() {
        return sid;
    }

    public CallerUsageExt setSid(int sid) {
        this.sid = sid;
        return this;
    }

    public Integer getConfigType() {
        return configType;
    }

    public CallerUsageExt setConfigType(Integer configType) {
        this.configType = configType;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public CallerUsageExt setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public CallerUsageExt setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
