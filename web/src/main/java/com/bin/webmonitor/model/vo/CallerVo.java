package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.component.StrJson;

import java.util.Date;

public class CallerVo  extends StrJson {

    private Integer id;

    private Integer cid;

    private String callername;

    private String owners;




    private String description;





    private Date createtime;

    private String createtimeStr;

    private Date updatetime;

    public CallerVo(){}

    public CallerVo(Builder builder) {
        this.id = builder.id;
        this.cid = builder.cid;
        this.callername = builder.callerName;
        this.owners = builder.owners;
        this.description = builder.description;
        this.createtime = builder.createTime;
        this.updatetime = builder.updateTime;
    }




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCallername() {
        return callername;
    }

    public void setCallername(String callername) {
        this.callername = callername;
    }

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }



    public void setCreatetime(Date createtime){
        this.createtime=createtime;
    }

    public Date getCreatetime(){
        return createtime;
    }

    public String getCreatetimeStr() {
        return createtimeStr;
    }

    public void setCreatetimeStr(String createtimeStr) {
        this.createtimeStr = createtimeStr;
    }

    public void setUpdatetime(Date updatetime){
        this.updatetime=updatetime;
    }

    public Date getUpdatetime(){
        return updatetime;
    }

    public static class Builder{
        private Integer id;
        private Integer cid;
        private String callerName;
        private String owners;

        private String description;
        private Date createTime;
        private Date updateTime;

        public Integer getId() {
            return id;
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Integer getCid() {
            return cid;
        }

        public Builder setCid(Integer cid) {
            this.cid = cid;
            return this;
        }

        public String getCallerName() {
            return callerName;
        }

        public Builder setCallerName(String callerName) {
            this.callerName = callerName;
            return this;
        }

        public String getOwners() {
            return owners;
        }

        public Builder setOwners(String owners) {
            this.owners = owners;
            return this;
        }



        public String getDescription() {
            return description;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }





        public Date getCreateTime() {
            return createTime;
        }

        public Builder setCreateTime(Date createtime) {
            this.createTime = createtime;
            return this;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public Builder setUpdateTime(Date updatetime) {
            this.updateTime = updatetime;
            return this;
        }

        public CallerVo builder(){
            return new CallerVo(this);
        }
    }
}