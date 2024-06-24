package com.bin.client.circuitbreak;

import com.bin.client.network.udp.UdpSender;
import com.bin.collector.request.CircuitBreakMonitorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CircuitBreakMonitorDataReport {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static CircuitBreakMonitorDataReport instance;
    
    public static CircuitBreakMonitorDataReport createInstance(String callerKey) {
        if (instance == null) {
            synchronized (CircuitBreakMonitorDataReport.class) {
                if (instance == null) {
                    instance = new CircuitBreakMonitorDataReport(callerKey);
                }
            }
        }
        return instance;
    }
    
    /**
     * 调用者密钥
     */
    private String callerKey;
    
    private CircuitBreakMonitorDataReport(String callerKey) {
        this.callerKey = callerKey;
    }
    
    /**
     * @方法名称 process
     * @功能描述 上报熔断监控数据
     * @param time 时间点
     * @param circuitBreakMonitorDatas 熔断监控数据列表
     */
    public void report(long time, List<CircuitBreakMonitorData.CircuitBreakMonitorData> circuitBreakMonitorDatas) {
        CircuitBreakMonitorData circuitBreakMonitorDataRequest = new CircuitBreakMonitorData(time, callerKey, circuitBreakMonitorDatas);
        UdpSender.fluxDataAndServiceFunctionSender.send(circuitBreakMonitorDataRequest);
        logger.debug("[ARCH_SDK_end_process_circuit_break_monitor_data]callerKey={},time={},circuitBreakMonitorDatas={}", callerKey, time, circuitBreakMonitorDatas);
    }
}
