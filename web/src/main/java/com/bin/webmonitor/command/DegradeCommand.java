package com.bin.webmonitor.command;

/**
 *  降级指令
 */
public class DegradeCommand extends BaseCommand {
    /**
     * 服务名
     */
    private String serviceName;
    
    public DegradeCommand() {
        this.code = Commands.DEGRADE;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public DegradeCommand setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DegradeCommand{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", code=").append(code);
        sb.append('}');
        return sb.toString();
    }
}
