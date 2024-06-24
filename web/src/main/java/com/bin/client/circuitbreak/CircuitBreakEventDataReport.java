package com.bin.client.circuitbreak;

import com.bin.client.network.udp.UdpSender;
import com.bin.collector.request.CircuitBreakEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakEventDataReport {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static CircuitBreakEventDataReport instance;
    
    public static CircuitBreakEventDataReport createInstance(String callerKey) {
        if (instance == null) {
            synchronized (CircuitBreakEventDataReport.class) {
                if (instance == null) {
                    instance = new CircuitBreakEventDataReport(callerKey);
                }
            }
        }
        return instance;
    }
    
    /**
     * 调用者密钥
     */
    private String callerKey;
    
    private CircuitBreakEventDataReport(String callerKey) {
        this.callerKey = callerKey;
    }
    
    /**
     * @方法名称 process
     * @功能描述 上报熔断事件数据
     * @param service 服务名称
     * @param method 方法名称
     * @param eventType 事件类型
     * @param eventTime 事件时间
     * @param reason 原因
     *
     */
    public void report(String service, String method, CircuitBreakEventData.EventType eventType, long eventTime, String reason) {
        CircuitBreakEventData circuitBreakEventDataRequest = new CircuitBreakEventData(callerKey, service, method, eventType, eventTime, reason);
        UdpSender.fluxDataAndServiceFunctionSender.send(circuitBreakEventDataRequest);
        logger.info("[ARCH_SDK_end_process_circuit_break_event]callerKey={},service={},method={},eventType={},eventTime={},reason={}", callerKey, service, method, eventType, eventTime, reason);
    }
}
