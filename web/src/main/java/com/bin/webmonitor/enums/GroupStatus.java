package com.bin.webmonitor.enums;

/**
 *  分组状态
 */
public enum GroupStatus {
    
    /**
     * 正常
     */
    Normal(1),
    
    /**
     * 无调用
     */
    NoUse(2);
    
    private int value;
    
    private GroupStatus(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static GroupStatus parse(int value) {
        for (GroupStatus tmp : values()) {
            if (value == tmp.value) {
                return tmp;
            }
        }
        throw new IllegalArgumentException("Illegal Argument [" + value + "] for GroupStatus");
    }
    
}