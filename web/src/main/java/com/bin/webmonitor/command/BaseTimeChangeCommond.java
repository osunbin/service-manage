package com.bin.webmonitor.command;

/**
 *  时间更新指令：统计 or 收集单位变更
 */
public class BaseTimeChangeCommond extends BaseCommand {
    private int baseTime;
    
    public BaseTimeChangeCommond() {
        this.code = Commands.BASE_TIME_CHANGE;
    }
    
    public int getBaseTime() {
        return baseTime;
    }
    
    public BaseTimeChangeCommond setBaseTime(int baseTime) {
        this.baseTime = baseTime;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseTimeChangeCommond{");
        sb.append("baseTime=").append(baseTime);
        sb.append('}');
        return sb.toString();
    }
}