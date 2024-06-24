package com.bin.webmonitor.enums;

/**
 *  关注类型
 */
public enum FeedType {
    /**
     * 服务入驻
     */
    ServiceJoin(1),
    /**
     * 调用者接入服务
     */
    CallerJoin(2),
    /**
     * 属性修改：服务属性，调用者关系属性
     */
    AttriEdit(3),
    /**
     * 限流：服务限流，服务函数限流
     */
    FluxControl(4),
    /**
     * 报警设置
     */
    AlertEdit(5),
    /**
     * 发送报警
     */
    SendAlert(6),
    /**
     * 函数开关
     */
    FunctionOnOff(7),
    /**
     * 其他
     */
    Other(0);
    
    private int value;
    
    private FeedType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static FeedType parse(int value) {
        for (FeedType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Illegal Argument [" + value + "] for FeedType");
    }
    
}
