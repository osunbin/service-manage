package com.bin.client.limiter;

import com.bin.client.model.ServerFluxDataRequest;
import com.bin.client.network.udp.UdpSender;
import com.bin.webmonitor.common.util.ThreadPool;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.enums.FluxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FluxControlReport {
    private static Logger logger = LoggerFactory.getLogger(FluxControlReport.class);
    /**
     * 限流流控Map
     */
    ConcurrentHashMap<String, ServerFluxDataRequest.ServerFluxData> functionCallerServerFluxData = new ConcurrentHashMap<String, ServerFluxDataRequest.ServerFluxData>();

    private static FluxControlReport instance = new FluxControlReport();

    public static FluxControlReport getInstance() {
        return instance;
    }

    /**
     * 流控数据每10ms上报一次
     */
    public FluxControlReport() {
        Runnable fluxControlSender = new Runnable() {
            @Override
            public void run() {
                try {
                    ConcurrentHashMap<String, ServerFluxDataRequest.ServerFluxData> map = functionCallerServerFluxData;
                    functionCallerServerFluxData = new ConcurrentHashMap<String, ServerFluxDataRequest.ServerFluxData>();
                    List<ServerFluxDataRequest.ServerFluxData> datas = new LinkedList<ServerFluxDataRequest.ServerFluxData>(map.values());
                    if (datas.size() == 0) {
                        return;
                    }

                    ServerFluxDataRequest serverFluxDataRequest = new ServerFluxDataRequest(TimeUtil.getMinuteMill(), "serviceName", datas);
                    UdpSender.fluxControlData.send(serverFluxDataRequest);
                    logger.debug("[ARCH_SDK_end_send_flux_control]serverFluxDataRequest={}", serverFluxDataRequest);
                } catch (Throwable e) {
                    logger.error("[ARCH_SDK_error_send_flux_control]", e);
                }

            }
        };
        ThreadPool.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(fluxControlSender, 10, 10, TimeUnit.SECONDS);
    }

    public void report(long function, long callerKey) {
        String key = generateKey(function, callerKey);
        ServerFluxDataRequest.ServerFluxData serverFluxData = functionCallerServerFluxData.get(key);
        if (serverFluxData == null) {
            synchronized (this) {
                serverFluxData = functionCallerServerFluxData.get(key);
                if (serverFluxData == null) {
                    serverFluxData = new ServerFluxDataRequest.ServerFluxData(function, callerKey, FluxType.Normal, 0, 0, 0);
                    functionCallerServerFluxData.putIfAbsent(key, serverFluxData);
                }
            }
        }
        serverFluxData.addCount();
    }

    private String generateKey(long function, long callerKey) {
        return function + "_" + callerKey;
    }
}
