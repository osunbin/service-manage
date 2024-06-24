package com.bin.webmonitor.enums;

public enum ReportType {
    /**
     *
     */
    fluxData((byte)1),
    /**
     * 上报服务方法
     */
    serviceFunction((byte)2),
    /**
     *
     */
    fluxControlData((byte)3),
    /**
     *
     */
    env((byte)4),
    /**
     * 服务端统计：[dubbo即为提供者]
     */
    SERVER_FLUXDATA((byte)5),
    /**
     * 客户端统计：[dubbo即为消费者]调用者采集项收集
     */
    CLIENT_FLUXDATA((byte)6),
    /**
     * 服务耗时分布统计
     */
    functionCostTimes((byte)7),
    /**
     * 服务SLA统计(含TP指标)
     */
    serviceSLACostTime((byte)8),
    /**
     *
     */
    testAlert((byte)9),
    /**
     * client端的熔断监控数据统计
     */
    circuitBreakMonitorData((byte)10),
    /**
     * client端的熔断事件
     */
    circuitBreakEventData((byte)11),
    /**
     * 函数SLA统计(含TP指标)
     */
    functionSLACostTime((byte)12);

    ReportType(byte b) {
        this.type = b;
    }

    private byte type;

    public byte getValue() {
        return type;
    }

    public static ReportType parse(byte requestType) {
        switch (requestType) {
            case 1:
                return fluxData;
            case 2:
                return serviceFunction;
            case 3:
                return fluxControlData;
            case 4:
                return env;
            case 5:
                return SERVER_FLUXDATA;
            case 6:
                return CLIENT_FLUXDATA;
            case 7:
                return functionCostTimes;
            case 8:
                return serviceSLACostTime;
            case 9:
                return testAlert;
            case 10:
                return circuitBreakMonitorData;
            case 11:
                return circuitBreakEventData;
            case 12:
                return functionSLACostTime;
            default:
                return null;
        }
    }

}
