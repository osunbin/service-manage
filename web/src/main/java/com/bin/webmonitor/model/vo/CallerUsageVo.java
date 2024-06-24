package com.bin.webmonitor.model.vo;

public class CallerUsageVo {

    private Integer id;

    private Integer cid;

    private Integer sid;

    private String svcName;

    private Integer gid;

    private Byte degrade;

    private Byte reject;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getSvcName() {
        return svcName;
    }

    public void setSvcName(String svcName) {
        this.svcName = svcName;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Byte getDegrade() {
        return degrade;
    }

    public void setDegrade(Byte degrade) {
        this.degrade = degrade;
    }

    public Byte getReject() {
        return reject;
    }

    public void setReject(Byte reject) {
        this.reject = reject;
    }



    @Override
    public String toString() {
        return "CallerUsageVo{" +
                "id=" + id +
                ", cid=" + cid +
                ", sid=" + sid +
                ", svcName='" + svcName + '\'' +
                ", gid=" + gid +
                ", degrade=" + degrade +
                ", reject=" + reject +
                '}';
    }
}
