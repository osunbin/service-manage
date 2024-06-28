package com.bin.client.network.udp;

import com.bin.collector.request.BaseData;
import com.bin.collector.request.CircuitBreakEventData;
import com.bin.collector.request.CircuitBreakMonitorDataRequest;
import com.bin.webmonitor.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UdpSender {
    private static Logger logger = LoggerFactory.getLogger(UdpSender.class);


    private static String collectorDomain = "flux.collector.com";

    private static String controlDomain = "flux.control.com";

    static {
        String collectord = System.getProperty("collectorDomain");
        String controld = System.getProperty("controlDomain");
        if (!StringUtil.isBlank(collectord)) {
            collectorDomain = collectord;
            logger.info("collector domain from env {}", collectorDomain);
        }
        if (!StringUtil.isBlank(controld)) {
            controlDomain = controld;
            logger.info("control domain from env {}", controlDomain);
        }
    }

    public static UdpSender fluxDataAndServiceFunctionSender = new UdpSender(collectorDomain, 28803);

    public static UdpSender fluxControlData = new UdpSender(controlDomain, 28804);

    private RetryAckSend retryAckSend;

    public UdpSender(String host, int port) {
        retryAckSend = new RetryAckSend(host, port);
    }

    public static void setFluxDomain(String fluxDomain) {
        if (fluxDomain != null && !fluxDomain.trim().equals("")) {
            logger.info("flux send to domain {}", fluxDomain);
            fluxDataAndServiceFunctionSender = new UdpSender(fluxDomain, 35559);
        }
    }

    public static void setFluxControlDomain(String fluxControlDomain) {
        if (fluxControlDomain != null && !fluxControlDomain.trim().equals("")) {
            logger.info("flux control send to domain {}", fluxControlDomain);
            fluxControlData = new UdpSender(fluxControlDomain, 35560);
        }
    }

    /**
     * @param request
     */
    public void send(BaseData request) {
        logger.debug("[ARCH_SDK_start_send_request]request={}", request);
        if (request == null) {
            return;
        }

         if (request instanceof CircuitBreakMonitorDataRequest) { // 熔断监控数据
            CircuitBreakMonitorDataRequest circuitBreakMonitorDataRequest = (CircuitBreakMonitorDataRequest)request;
            // 分包逻辑
            List<CircuitBreakMonitorDataRequest> requestList = circuitBreakMonitorDataRequest.split();
            for (CircuitBreakMonitorDataRequest tmp : requestList) {
                this.retryAckSend.send(tmp.toBytes(),  true);
            }
        } else if (request instanceof CircuitBreakEventData) { // 熔断事件
            this.retryAckSend.send(request.toBytes(),  true);
        }

    }
}
