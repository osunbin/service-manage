package com.bin.webmonitor.enums;

/**
 *  降级类型
 */
public enum DegradeType {
    /**
     * 正常
     */
    Normal(0),
    /**
     * 服务降级
     */
    ServiceDegrade(1),
    /**
     * 方法降级
     */
    FuncDegrade(2);
    
    private int value;
    
    private DegradeType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static DegradeType parse(int value) {
        for (DegradeType type : values()) {
            if (value == type.value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Illegal Argument [" + value + "] for FeedType");
    }
}
