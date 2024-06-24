package com.bin.webmonitor.enums;

public enum CircuitBreakEventType {
    /**
     * 熔断器关闭
     */
    OFF((byte)0, "熔断器关闭"),
    /**
     * 熔断器打开
     */
    ON((byte)1, "熔断器打开"),
    /**
     * 服务重启
     */
    REBOOT((byte)2, "服务重启"),;

    private final byte code;

    private final String desc;

    CircuitBreakEventType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CircuitBreakEventType codeOf(byte code) {
        for (CircuitBreakEventType eventType : CircuitBreakEventType.values()) {
            if (eventType.code == code) {
                return eventType;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
