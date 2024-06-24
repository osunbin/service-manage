package com.bin.client.model;

public class ExecutableResult {
    /**
     *  是否执行
     */
    private boolean executable;

    /**
     * 拒绝原因
     */
    private RejectionReason reason;

    public boolean isExecutable() {
        return executable;
    }

    public ExecutableResult setExecutable(boolean executable) {
        this.executable = executable;
        return this;
    }

    public RejectionReason getReason() {
        return reason;
    }

    public ExecutableResult setReason(RejectionReason reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public String toString() {
        return "serverExecutableResult{" + "executable=" + executable + ", reason=" + reason + '}';
    }
}
