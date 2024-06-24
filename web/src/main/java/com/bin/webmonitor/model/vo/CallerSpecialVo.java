package com.bin.webmonitor.model.vo;

public class CallerSpecialVo {

    private int id;

    private int sid;

    private int cid;

    private String cips;

    private String serialize;

    private String sips;



    private String serviceName;

    private String callerName ;

    private String createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCips() {
        return cips;
    }

    public void setCips(String cips) {
        this.cips = cips;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public String getSips() {
        return sips;
    }

    public void setSips(String sips) {
        this.sips = sips;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }



    @Override
    public String toString() {
        return "CallerSpecialVo{" +
            "id=" + id +
            ", sid=" + sid +
            ", cid=" + cid +
            ", cips='" + cips + '\'' +
            ", serialize='" + serialize + '\'' +
            ", sips='" + sips + '\'' +
            ", serviceName='" + serviceName + '\'' +
            ", callerName='" + callerName + '\'' +
            ", createTime='" + createTime + '\'' +
            '}';
    }

}
