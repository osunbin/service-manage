package com.bin.collector.request;

import com.bin.webmonitor.enums.ReportType;

public class CircuitBreakEventData extends BaseData {
    /**
     * 调用者密钥
     */
    private String callerKey;
    
    /**
     * 服务名称
     */
    private String service;
    
    /**
     * 方法名称
     */
    private String method;
    
    /**
     * 事件类型
     */
    private EventType eventType;
    
    /**
     * 发生时间
     */
    private long eventTime;
    
    /**
     * 事件原因(64字以内)
     */
    private String reason;
    
    public CircuitBreakEventData() {
        this.reportType = ReportType.circuitBreakEventData;
    }
    
    public CircuitBreakEventData(String callerKey, String service, String method, EventType eventType, long eventTime, String reason) {
        this.reportType = ReportType.circuitBreakEventData;
        this.callerKey = callerKey;
        this.service = service;
        this.method = method;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.reason = reason;
    }

    
    public String getCallerKey() {
        return callerKey;
    }
    
    public void setCallerKey(String callerKey) {
        this.callerKey = callerKey;
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
    
    public EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    
    public long getEventTime() {
        return eventTime;
    }
    
    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public String toString() {
        return "CircuitBreakEventDataRequest{" + "callerKey='" + callerKey + '\'' + ", service='" + service + '\'' + ", method='" + method + '\'' + ", eventType=" + eventType + ", eventTime=" + eventTime + ", reason='" + reason + '\'' + '}';
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public CircuitBreakEventData toRequest(byte[] b) {
        return null;
    }

    /**
     * 熔断事件类型
     */
    public enum EventType {
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
        
        EventType(byte code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
        public static EventType codeOf(byte code) {
            for (EventType eventType : EventType.values()) {
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
}
