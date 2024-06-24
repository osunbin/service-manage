package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.repository.domain.ServiceFunction;

import java.util.List;

public class CallerConfigVo {

    private Integer cid;

    private String callerName;

    private Integer sid;

    private String svcName;

    private Integer gid;

    private Integer quantity;

    private String description;


    private String protocolserialize;


    private String protocolcompresstype;

    private String protocolprotocoltype;

    private String serverdeadtimeout;

    private Byte degrade;


    private Byte reject;

    private Integer granularity;

    private List<CallerFuncUsageVo> funcUsageVoList;

    private List<ServiceFunction> notApply;

    public List<ServiceFunction> getNotApply() {
        return notApply;
    }

    public CallerConfigVo setNotApply(List<ServiceFunction> notApply) {
        this.notApply = notApply;
        return this;
    }

    @Override
    public String toString() {
        return "CallerConfigVo{" +
                "cid=" + cid +
                ", callerName='" + callerName + '\'' +
                ", sid=" + sid +
                ", svcName='" + svcName + '\'' +
                ", gid=" + gid +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", reject=" + reject +
                ", funcUsageVoList size=" + funcUsageVoList.size() +
                '}';
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getProtocolcompresstype() {
        return protocolcompresstype;
    }

    public void setProtocolcompresstype(String protocolcompresstype) {
        this.protocolcompresstype = protocolcompresstype;
    }

    public String getProtocolprotocoltype() {
        return protocolprotocoltype;
    }

    public void setProtocolprotocoltype(String protocolprotocoltype) {
        this.protocolprotocoltype = protocolprotocoltype;
    }

    public String getServerdeadtimeout() {
        return serverdeadtimeout;
    }

    public void setServerdeadtimeout(String serverdeadtimeout) {
        this.serverdeadtimeout = serverdeadtimeout;
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

    public Integer getGranularity() {
        return granularity;
    }

    public void setGranularity(Integer granularity) {
        this.granularity = granularity;
    }

    public List<CallerFuncUsageVo> getFuncUsageVoList() {
        return funcUsageVoList;
    }

    public String getServiceName() {
        return svcName;
    }

    public void setFuncUsageVoList(
            List<CallerFuncUsageVo> funcUsageVoList) {
        this.funcUsageVoList = funcUsageVoList;
    }
}
