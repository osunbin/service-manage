package com.bin.webmonitor.model.vo;

public class CallerFuncUsageVo {

    private Integer fid;

    private Integer quantity;

    private String funcName;

    private Boolean degrade;

    public CallerFuncUsageVo() {
    }

    public CallerFuncUsageVo(Integer id, Integer quantity, String funcName, Boolean degrade) {
        this.fid = id;
        this.quantity = quantity;
        this.funcName = funcName;
        this.degrade = degrade;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public Boolean getDegrade() {
        return degrade;
    }

    public void setDegrade(Boolean degrade) {
        this.degrade = degrade;
    }

    @Override
    public String toString() {
        return "CallerFuncUsageVo{" +
            "fid=" + fid +
            ", quantity=" + quantity +
            ", funcName='" + funcName + '\'' +
            ", degrade=" + degrade +
            '}';
    }
}