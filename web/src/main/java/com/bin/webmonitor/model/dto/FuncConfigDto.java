package com.bin.webmonitor.model.dto;

import com.google.common.base.Objects;

public class FuncConfigDto {

    private Integer callFuncUsageId;

    private String groupName;

    private String funcName;

    private String quantity;

    private Integer delta;

    public FuncConfigDto() {
    }

    public FuncConfigDto(String groupName, String funcName, String quantity, Integer delta) {
        this.groupName = groupName;
        this.funcName = funcName;
        this.quantity = quantity;
        this.delta = delta;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public Integer getCallFuncUsageId() {
        return callFuncUsageId;
    }

    public void setCallFuncUsageId(Integer callFuncUsageId) {
        this.callFuncUsageId = callFuncUsageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FuncConfigDto)) {
            return false;
        }
        FuncConfigDto that = (FuncConfigDto) o;
        return Objects.equal(getGroupName(), that.getGroupName()) &&
            Objects.equal(getFuncName(), that.getFuncName()) &&
            Objects.equal(getQuantity(), that.getQuantity()) &&
            Objects.equal(getDelta(), that.getDelta());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getGroupName(), getFuncName(), getQuantity(), getDelta());
    }

    @Override
    public String toString() {
        return "FuncConfigDto{" +
            "groupName='" + groupName + '\'' +
            ", funcName='" + funcName + '\'' +
            ", quantity='" + quantity + '\'' +
            ", delta=" + delta +
            '}';
    }

}
