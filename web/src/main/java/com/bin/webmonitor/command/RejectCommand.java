package com.bin.webmonitor.command;

/**
 *  拒绝指令
 */
public class RejectCommand extends BaseCommand {
    /**
     * 调用者密钥
     */
    private String callerKey;
    
    public RejectCommand() {
        this.code = Commands.REJECT;
    }
    
    public String getCallerKey() {
        return callerKey;
    }
    
    public RejectCommand setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RejectCommand{");
        sb.append("callerKey='").append(callerKey).append('\'');
        sb.append(", code=").append(code);
        sb.append('}');
        return sb.toString();
    }
}
