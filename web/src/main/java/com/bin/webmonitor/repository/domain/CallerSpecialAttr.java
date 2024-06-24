package com.bin.webmonitor.repository.domain;

import com.bin.webmonitor.common.util.JsonHelper;

import java.util.Date;

public class CallerSpecialAttr {
    
    private int id;
    
    /**
     * 服务id
     */
    private int sid;
    
    /**
     * 调用方id
     */
    private int cid;
    
    /**
     * 客户端灰度ip列表
     */
    private String cips;
    
    /**
     * 扩展属性
     */
    private String attrJson;
    
    private Date createTime;
    
    private Date updateTime;
    
    /***********************************/
    /**
     * 修改权限
     */
    private boolean update;
    
    /**
     * 服务名
     */
    private String serviceName;
    
    /**
     * 调用名
     */
    private String callerName;
    
    public Attributes getAttributes() {
        return JsonHelper.fromJson(this.attrJson, Attributes.class);
    }
    
    public void setAttributes(Attributes attributes) {
        this.attrJson = JsonHelper.toJson(attributes);
    }
    
    public int getId() {
        return id;
    }
    
    public CallerSpecialAttr setId(int id) {
        this.id = id;
        return this;
    }
    
    public int getSid() {
        return sid;
    }
    
    public CallerSpecialAttr setSid(int sid) {
        this.sid = sid;
        return this;
    }
    
    public int getCid() {
        return cid;
    }
    
    public CallerSpecialAttr setCid(int cid) {
        this.cid = cid;
        return this;
    }
    
    public String getCips() {
        return cips;
    }
    
    public CallerSpecialAttr setCips(String cips) {
        this.cips = cips;
        return this;
    }
    
    public String getAttrJson() {
        return attrJson;
    }
    
    public CallerSpecialAttr setAttrJson(String attrJson) {
        this.attrJson = attrJson;
        return this;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public CallerSpecialAttr setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public CallerSpecialAttr setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    public boolean isUpdate() {
        return update;
    }
    
    public CallerSpecialAttr setUpdate(boolean update) {
        this.update = update;
        return this;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public CallerSpecialAttr setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public String getCallerName() {
        return callerName;
    }
    
    public CallerSpecialAttr setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }
    
    public static class Attributes {
        
        private String serialize;
        
        private String sips;
        
        public String getSips() {
            return sips;
        }
        
        public void setSips(String sips) {
            this.sips = sips;
        }
        
        public String getSerialize() {
            return serialize;
        }
        
        public void setSerialize(String serialize) {
            this.serialize = serialize;
        }
        
        @Override
        public String toString() {
            return "Attributes{" + "Serialize='" + serialize + '\'' + ", sips='" + sips + '\'' + '}';
        }
    }
    
}
