package com.bin.webmonitor.naming;

import java.io.Serializable;

public class GiveCommand implements Serializable {
    
    private static final long serialVersionUID = 1L;
    

    /**
     * 指令流向
     */
    private boolean serverMode;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 调用者秘钥
     */
    private String callerKey;
    
    /**
     * 内容
     */
    private String content;
    
    public GiveCommand() {
    }
    
    public GiveCommand(boolean mode, String serviceName, String callerKey, String content) {
        this.serverMode = mode;
        this.serviceName = serviceName;
        this.callerKey = callerKey;
        this.content = content;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public GiveCommand setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public String getContent() {
        return content;
    }
    
    public GiveCommand setContent(String content) {
        this.content = content;
        return this;
    }


    public boolean isServerMode() {
        return serverMode;
    }

    public GiveCommand setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
        return this;
    }

    public String getCallerKey() {
        return callerKey;
    }
    
    public GiveCommand setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Command{");
        sb.append(", serverMode=").append(serverMode);
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", callerKey='").append(callerKey).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
