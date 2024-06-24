package com.bin.webmonitor.repository.domain;

import com.bin.webmonitor.component.StrJson;

import java.util.Date;

/**
 *  审计日志对象
 */
public class OperateRecord extends StrJson {
    private Integer id;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 收集器id
     */
    private Integer cid;

    /**
     * 服务id
     */
    private Integer sid;

    /**
     * 操作类型
     */
    private Integer opType;

    /**
     * 操作内容
     */
    private String content;

    private Date createTime;

    private Date updateTime;

    public OperateRecord() {
    }

    public OperateRecord(String uid, int cid, int sid, int opType, String content, Date createTime) {
        this.uid = uid;
        this.cid = cid;
        this.sid = sid;
        this.opType = opType;
        this.content = content;
        this.createTime = createTime;
    }

    public OperateRecord(Builder builder) {
        this.id = builder.id;
        this.uid = builder.uid;
        this.cid = builder.cid;
        this.sid = builder.sid;
        this.opType = builder.opType;
        this.content = builder.content;
        this.createTime = builder.createTime;
        this.updateTime = builder.updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public static class Builder {
        Integer id;

        String uid;

        Integer cid;

        Integer sid;

        Integer opType;

        String content;

        Date createTime;

        Date updateTime;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setuid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder setCid(Integer cid) {
            this.cid = cid;
            return this;
        }

        public Builder setSid(Integer sid) {
            this.sid = sid;
            return this;
        }

        public Builder setOpType(Integer opType) {
            this.opType = opType;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public OperateRecord builder() {
            return new OperateRecord(this);
        }
    }

}
