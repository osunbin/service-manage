package com.bin.collector;

import com.bin.collector.request.CircuitBreakMonitorData;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalFunction;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CircuitBreakMonitorDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CircuitBreakMonitor;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *  熔断监控数据收集器处理
 */
@Component
public class CircuitBreakMonitorCollector {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreakMonitorCollector.class);
    @Autowired
    LocalFunction localFunction;
    @Autowired
    LocalCaller localCaller;

    @Autowired
    LocalService localService;
    @Autowired
    CircuitBreakMonitorDao circuitBreakMonitorDao;


    public void collect(CircuitBreakMonitorData request, String ip) {
        logger.debug("op=start_process,request={},ip={}", request, ip);
        Caller caller = localCaller.getByCallerKey(request.getCallerKey());

        List<CircuitBreakMonitor> circuitBreakMonitorPos =
                new ArrayList<>(request.getCircuitBreakMonitorDatas().size());

        // 1.类型转换
        for (CircuitBreakMonitorData.CircuitBreakMonitorData circuitBreakMonitorData : request.getCircuitBreakMonitorDatas()) {
            try {
                ServiceInstance service = localService.getByName(circuitBreakMonitorData.getService());
                ServiceFunction serviceFunction =
                        localFunction.serviceFunctionByName(service.getId(), circuitBreakMonitorData.getMethod());

                CircuitBreakMonitor circuitBreakMonitorPo = new CircuitBreakMonitor();
                circuitBreakMonitorPo.setCid(caller.getId());
                circuitBreakMonitorPo.setSid(service.getId());
                circuitBreakMonitorPo.setFid(serviceFunction.getId());
                circuitBreakMonitorPo.setIp(ip);
                circuitBreakMonitorPo.setSuccessCount(circuitBreakMonitorData.getSuccessCount());
                circuitBreakMonitorPo.setFailCount(circuitBreakMonitorData.getFailCount());
                circuitBreakMonitorPo.setTimeoutCount(circuitBreakMonitorData.getTimeoutCount());
                circuitBreakMonitorPo.setStatus(circuitBreakMonitorData.getStatus());
                circuitBreakMonitorPo.setMinTime(request.getTime());

                circuitBreakMonitorPos.add(circuitBreakMonitorPo);
            } catch (Exception e) {
                logger.error("[ERROR-transfrom-CircuitBreakMonitorPo]circuitBreakMonitorData" + "={}",
                        circuitBreakMonitorData, e);
            }

        }

        // 2.批量入库
        int count = circuitBreakMonitorDao.batchSave(circuitBreakMonitorPos);
        logger.info("op=end_process,request={},ip={},count={}", request, ip, count);
    }

}
