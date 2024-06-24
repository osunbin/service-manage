package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.repository.domain.ServiceFunction;

import java.util.List;

public class ServiceFunctionGroupVO {

    private String groupName;
    private int gid;
    private int functionNum;
    private List<ServiceFunction> functions;
    private List<ServiceFunction> canSelect;
    private boolean update;

    public String getGroupName() {
        return groupName;
    }

    public ServiceFunctionGroupVO setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public int getFunctionNum() {
        return functionNum;
    }

    public ServiceFunctionGroupVO setFunctionNum(int functionNum) {
        this.functionNum = functionNum;
        return this;
    }

    public List<ServiceFunction> getFunctions() {
        return functions;
    }

    public ServiceFunctionGroupVO setFunctions(List<ServiceFunction> functions) {
        this.functions = functions;
        return this;
    }

    public List<ServiceFunction> getCanSelect() {
        return canSelect;
    }

    public ServiceFunctionGroupVO setCanSelect(List<ServiceFunction> canSelect) {
        this.canSelect = canSelect;
        return this;
    }

    public int getGid() {
        return gid;
    }

    public ServiceFunctionGroupVO setGid(int gid) {
        this.gid = gid;
        return this;
    }


    public boolean isUpdate() {
        return update;
    }

    public ServiceFunctionGroupVO setUpdate(boolean update) {
        this.update = update;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceFunctionGroupVO{");
        sb.append("groupName='").append(groupName).append('\'');
        sb.append(", functionNum=").append(functionNum);
        sb.append(", functions=").append(functions);
        sb.append(", canSelect=").append(canSelect);
        sb.append('}');
        return sb.toString();
    }
}
