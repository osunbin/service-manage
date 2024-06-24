package com.bin.collector;

import com.bin.collector.request.CircuitBreakEventData;
import com.bin.collector.request.CircuitBreakEventData.EventType;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalFunction;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CircuitBreakEventDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CircuitBreakEvent;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *  熔断事件收集器处理
 */
@Component
public class CircuitBreakEventCollector {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreakEventCollector.class);

    @Autowired
    LocalFunction localFunction;

    @Autowired
    LocalCaller localCaller;



    @Autowired
    LocalService localService;

    @Autowired
    CircuitBreakEventDao circuitBreakEventDao;

    @Autowired
    private Environment environment;


    public void collect(CircuitBreakEventData request, String ip) {
        // 1.转换类型
        CircuitBreakEvent circuitBreakEventPo = buildCircuitBreakEvent(request, ip);

        // 2.入库
        int count = circuitBreakEventDao.batchSave(Collections.singletonList(circuitBreakEventPo));
        logger.debug("op=end_process,request={},context={},count={}", request, ip, count);

        // 3.报警
        if (request.getEventType() == CircuitBreakEventData.EventType.OFF || request.getEventType() == CircuitBreakEventData.EventType.ON) {
            sendAlarmMsg(request, ip);
        }

    }

    public static final List<String> DEVELOPER = List.of("duyunjie", "zhaoziyan");

    private void sendAlarmMsg(CircuitBreakEventData request, String ip) {
        Caller caller = localCaller.getByCallerKey(request.getCallerKey());

        StringBuilder builder = new StringBuilder();
        builder.append("服务熔断报警-" + getEnvironmentStr() + "\n");
        builder.append(request.getEventType() == EventType.ON ? "<div class='highlight'>" : "<div>");
        builder.append(String.format("调用方：%s\n", caller.getCallerName()));
        builder.append(String.format("调用方IP：%s\n", ip));

        builder.append(String.format("服务方：%s\n", request.getService()));
        builder.append(String.format("接口名称：%s\n", request.getMethod()));

        builder.append(String.format("事件：%s\n", request.getEventType().getDesc()));
        builder.append(String.format("原因：%s\n", request.getReason()));

        builder.append(String.format("发生时间：%s\n", TimeUtil.date2fullStr(new Date(request.getEventTime()))));
        builder.append("</div>");


        // TODO 报警
        logger.warn("builder.toString()");
    }

    private CircuitBreakEvent buildCircuitBreakEvent(CircuitBreakEventData request, String ip) {
        CircuitBreakEvent res = new CircuitBreakEvent();
        Caller caller = localCaller.getByCallerKey(request.getCallerKey());
        ServiceInstance service = localService.getByName(request.getService());
        ServiceFunction serviceFunction = localFunction.serviceFunctionByName(service.getId(), request.getMethod());

        res.setCid(caller.getId());
        res.setSid(service.getId());
        res.setFid(serviceFunction.getId());
        res.setIp(ip);

        res.setEventType(request.getEventType().getCode());
        res.setEventTime(request.getEventTime());
        res.setReason(request.getReason());

        return res;
    }

    private String getEnvironmentStr() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles == null || activeProfiles.length == 0) {
            return "未知环境";
        }
        String activeProfile = activeProfiles[0];
        if ("dev".equals(activeProfile)) {
            return "开发环境";
        }
        if ("test".equals(activeProfile)) {
            return "测试环境";
        }
        if ("prod".equals(activeProfile)) {
            return "线上环境";
        }
        return "未知环境[" + activeProfile + "]";
    }
}
