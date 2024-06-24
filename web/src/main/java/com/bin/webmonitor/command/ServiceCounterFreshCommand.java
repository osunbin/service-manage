package com.bin.webmonitor.command;

/**
 *  服务更新指令：服务提供方节点变更
 */
public class ServiceCounterFreshCommand extends BaseCommand {
    
    public ServiceCounterFreshCommand() {
        this.code = Commands.SERVICE_COUNTER;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceCounterFreshCommand{");
        sb.append("code=").append(code);
        sb.append('}');
        return sb.toString();
    }
}
