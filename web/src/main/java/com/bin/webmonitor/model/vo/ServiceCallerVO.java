package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.repository.domain.Group;

import java.util.List;

public class ServiceCallerVO {

    private String callerName;
    private int cid;
    private String groupName;
    private int groupId;
    private boolean reject;//调用状态
    private String function;
    private boolean update;
    private List<Group> groups;
    private List<String> functions;

    public List<String> getFunctions() {
        return functions;
    }

    public ServiceCallerVO setFunctions(List<String> functions) {
        this.functions = functions;
        return this;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public ServiceCallerVO setGroups(List<Group> groups) {
        this.groups = groups;
        return this;
    }

    public String getFunction() {
        return function;
    }

    public ServiceCallerVO setFunction(String function) {
        this.function = function;
        return this;
    }

    public String getCallerName() {
        return callerName;
    }

    public ServiceCallerVO setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }

    public int getCid() {
        return cid;
    }

    public ServiceCallerVO setCid(int cid) {
        this.cid = cid;
        return this;
    }



    public String getGroupName() {
        return groupName;
    }

    public ServiceCallerVO setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public int getGroupId() {
        return groupId;
    }

    public ServiceCallerVO setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    public boolean isReject() {
        return reject;
    }

    public ServiceCallerVO setReject(boolean reject) {
        this.reject = reject;
        return this;
    }

    public boolean isUpdate() {
        return update;
    }

    public ServiceCallerVO setUpdate(boolean update) {
        this.update = update;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceCallerVO{");
        sb.append("callerName='").append(callerName).append('\'');
        sb.append(", cid=").append(cid);
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", groupId=").append(groupId);
        sb.append(", reject=").append(reject);
        sb.append(", function='").append(function).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
