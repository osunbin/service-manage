package com.bin.webmonitor.command;

/**
 *  配置更新指令
 */
public class ConfigChangeCommand extends BaseCommand {
    private String service;
    
    public ConfigChangeCommand() {
        this.code = Commands.CONFIG_CHANGE;
    }
    
    public String getService() {
        return service;
    }
    
    public ConfigChangeCommand setService(String service) {
        this.service = service;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConfigChangeCommand{");
        sb.append("service='").append(service).append('\'');
        sb.append(", code=").append(code);
        sb.append('}');
        return sb.toString();
    }
}
