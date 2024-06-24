package com.bin.webmonitor.command;

/**
 *  熔断配置指令:严格起来属于配置更新指令一部分
 */
public class CircuitBreakConfigChangeCommand extends BaseCommand {
    /**
     * 服务名称
     */
    private String service;
    
    /**
     * 方法名称 
     */
    private String method;
    
    public CircuitBreakConfigChangeCommand() {
        this.code = Commands.CIRCUIT_BREAK_CONFIG_CHANGE;
    }
    
    public CircuitBreakConfigChangeCommand(String service, String method) {
        this.code = Commands.CIRCUIT_BREAK_CONFIG_CHANGE;
        this.service = service;
        this.method = method;
    }
    
    public String getService() {
        return service;
    }
    
    public void setService(String service) {
        this.service = service;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    @Override
    public String toString() {
        return "CircuitBreakConfigChangeCommand{" + "service='" + service + '\'' + ", method='" + method + '\'' + '}';
    }
}
