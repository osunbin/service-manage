package com.bin.webmonitor.enums;

/**
 *  函数(方法)状态
 */
public enum FunctionStatus {
    /**
     * 关闭
     */
    close(0),
    /**
     * 开启
     */
    open(1);
    
    private int value;
    
    public int getValue() {
        return this.value;
    }
    
    private FunctionStatus(int value) {
        this.value = value;
    }
    
    public FunctionStatus parse(int value) {
        for (FunctionStatus type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Illegal Argument [" + value + "] for FunctionStatus");
    }
}
