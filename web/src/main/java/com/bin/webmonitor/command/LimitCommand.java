package com.bin.webmonitor.command;

/**
 *  限流指令
 */
public class LimitCommand extends BaseCommand {
    /**
     * 调用者密钥
     */
    private String callerKey;
    
    /**
     * 调用函数
     */
    private String function;
    
    /**
     * 限流时长
     */
    private long time;
    
    public LimitCommand() {
        this.code = Commands.LIMIT;
    }
    
    public String getCallerKey() {
        return callerKey;
    }
    
    public LimitCommand setCallerKey(String callerKey) {
        this.callerKey = callerKey;
        return this;
    }
    
    public String getFunction() {
        return function;
    }
    
    public LimitCommand setFunction(String function) {
        this.function = function;
        return this;
    }
    
    public long getTime() {
        return time;
    }
    
    public LimitCommand setTime(long time) {
        this.time = time;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LimitCommand{");
        sb.append("callerKey='").append(callerKey).append('\'');
        sb.append(", function='").append(function).append('\'');
        sb.append(", time=").append(time);
        sb.append(", code=").append(code);
        sb.append('}');
        return sb.toString();
    }
}
