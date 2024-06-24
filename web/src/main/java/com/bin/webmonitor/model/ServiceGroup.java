package com.bin.webmonitor.model;

import com.bin.webmonitor.repository.domain.Group;

import java.util.List;

public class ServiceGroup {
    private Group group;
    private boolean inUse; //调用状态
    private boolean updateable;
    private List<String> canSelectIps;
    private List<String> groupIps;

    public Group getGroup() {
        return group;
    }

    public ServiceGroup setGroup(Group group) {
        this.group = group;
        return this;
    }

    public boolean isInUse() {
        return inUse;
    }

    public ServiceGroup setInUse(boolean inUse) {
        this.inUse = inUse;
        return this;
    }

    public List<String> getCanSelectIps() {
        return canSelectIps;
    }

    public ServiceGroup setCanSelectIps(List<String> canSelectIps) {
        this.canSelectIps = canSelectIps;
        return this;
    }

    public List<String> getGroupIps() {
        return groupIps;
    }

    public ServiceGroup setGroupIps(List<String> groupIps) {
        this.groupIps = groupIps;
        return this;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public ServiceGroup setUpdateable(boolean updateable) {
        this.updateable = updateable;
        return this;
    }

    @Override
    public String toString() {
        return "ServiceGroup{" + "group=" + group + ", inUse=" + inUse + ", updateable=" + updateable
            + ", canSelectIps=" + canSelectIps + ", groupIps=" + groupIps + '}';
    }
}
