package com.bin.client.degrade;

import com.bin.client.circuitbreak.CircuitBreakEventDataReport;
import com.bin.client.circuitbreak.CircuitBreakMonitorDataReport;
import com.bin.client.ClientService;
import com.bin.client.circuitbreak.CircuitBreakConfigChangeListener;
import com.bin.client.degrade.MetricTimeWindow.MetricEventType;
import com.bin.client.util.MethodUtils;
import com.bin.collector.request.CircuitBreakEventData.EventType;
import com.bin.collector.request.CircuitBreakMonitorData.CircuitBreakMonitorData;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("all")
public class ClientDegradeManager implements CircuitBreakConfigChangeListener {

    private static Logger logger = LoggerFactory.getLogger(ClientDegradeManager.class);
    private static final String delimiter = "@";
    private static final String fallbackPostfix = "Fallback";
    private static ConcurrentHashMap<String, MetricTimeWindow> metricTimeWindowMap = new ConcurrentHashMap<String, MetricTimeWindow>();
    private static ConcurrentHashMap<String, Object> fallbackMethod = new ConcurrentHashMap<String, Object>();
    private static ClientService clientService;
    private static CircuitBreakMonitorDataReport monitorDataReporter;
    private String callerKey;
    private ScheduledExecutorService scheService;
    private static CircuitBreakEventDataReport circuitBreakEventReport;


    public ClientDegradeManager(ClientService clientService, String callerKey) {
        logger.info("ClientDegradeManager create");
        clientService = clientService;
        this.callerKey = callerKey;
        monitorDataReporter = CircuitBreakMonitorDataReport.createInstance(callerKey);
        circuitBreakEventReport = CircuitBreakEventDataReport.createInstance(callerKey);
        scheService = Executors.newScheduledThreadPool(2, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                //  Auto-generated method stub
                Thread thread = new Thread(r, "ClientDegradeReportThread");
                return thread;
            }
        });
        ReportRunnable reportRunable = new ReportRunnable();
        scheService.scheduleAtFixedRate(reportRunable, 60, 60, TimeUnit.SECONDS);
        Executor exe = Executors.newFixedThreadPool(5, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                //  Auto-generated method stub
                int num = index.incrementAndGet();
                Thread thread = new Thread(r, "ClientDegradeEventThread-" + num);
                return thread;
            }
        });
        logger.info("ClientDegradeManager created success");
    }

    public static DegradeResult degradeFilter(String serviceName, String methodName, Method method,Object[] params) {
        try {
            DegradeResult result = doDegradeFilter(serviceName, methodName, method,params);
            return result;
        } catch (Exception ex) {
            logger.error("check degradeFilter fail", ex);
        }
        DegradeResult result = new DegradeResult(DegradeStatus.benormal);
        return result;
    }

    private static DegradeResult doDegradeFilter(String serviceName, String methodName, Method method,Object[] params) {
        logger.debug("start DegradeFilter serviceName {} method {} ", serviceName, methodName);
        long start = System.currentTimeMillis();
        String key = serviceName + delimiter + methodName;
        MetricTimeWindow metricTimeWindow = metricTimeWindowMap.get(key);
        if (metricTimeWindow == null) {
            if (clientService != null) {
                CircuitBreakConfigMeta config = clientService.getCircuitBreakConfig(serviceName, methodName);
                if (config != null && config.isEnabled()) {
                    MetricTimeWindow check = metricTimeWindowMap.get(key);
                    if (check == null) {
                        MetricTimeWindow timeWindow = new MetricTimeWindow(serviceName, methodName, clientService,
                                config.getSlideWindowInSeconds(), (event) -> listen(event));
                        MetricTimeWindow resWindow = metricTimeWindowMap.putIfAbsent(key, timeWindow);
                        if (resWindow == null)
                            logger.info("DegradeManager add {} metrictimewindow config {} ", key, config);
                    }
                }
            }
            DegradeResult result = new DegradeResult(DegradeStatus.benormal);
            logger.debug("start DegradeFilter serviceName {} method {} result {} ", serviceName, methodName, result);
            return result;
        }
        boolean canContinue = metricTimeWindow.getCircuitBreaker().attemptExecution();
        if (canContinue) {
            DegradeResult result = new DegradeResult(DegradeStatus.benormal);
            logger.debug("start DegradeFilter serviceName {} method {} result {} ", serviceName, methodName, result);
            return result;
        } else {
            Object object = fallbackMethod.get(key);
            DegradeResult degradeRes = new DegradeResult(DegradeStatus.beDgraded);
            logger.error("serviceName {} method {} trigger clientDgraded", serviceName, methodName);
            if (object != null) {
                try {
                    Object resObj = method.invoke(object, params);
                    degradeRes.setObj(resObj);
                } catch (Exception ex) {
                    logger.error("invoke fallback method fail ", ex);
                }
            }
            // accurate to 0.1 ms
            int cost = (int) (System.currentTimeMillis() - start);
            logger.debug("start DegradeFilter serviceName {} method {} result {} cost:{}", serviceName, methodName, degradeRes, cost);

            return degradeRes;
        }
    }

    /**
     * when caller have the exception
     */
    public static DegradeResult fallbackFilter(String serviceName, String methodName, Method method,Object[] params, Throwable callerException) {
        logger.trace("start fallbackFilter serviceName {} method {} ", serviceName, methodName);
        DegradeResult degradeRes = new DegradeResult(DegradeStatus.benormal);
        String key = serviceName + delimiter + methodName;
        Object object = fallbackMethod.get(key);
        if (object != null) {
            try {
                logger.error("service {}  method {} RPC caller exception so call fallback method , original exception ", serviceName,
                        methodName, callerException);

                Object resObj = method.invoke(object, params);
                degradeRes.setObj(resObj);
                degradeRes.setStatus(DegradeStatus.befallback);
            } catch (Exception ex) {
                logger.error("service {}  method {} invoke fallback method fail ", serviceName, methodName, ex);
            }
        }
        return degradeRes;
    }

    public static void registerFallBack(String serviceName, Object object, Method method) {

        String methodSignature = MethodUtils.generateMethodKey( method, true);
        String key = serviceName + "@" + methodSignature;
        if (object != null) {
            fallbackMethod.put(key, object);
            logger.info("find fallback method {} object {}", key, object);
        }
    }

    public static void addEvent(String serviceName, String methodName, MetricTimeWindow.MetricEventType event) {
        String str = serviceName + delimiter + methodName;
        MetricTimeWindow metric = metricTimeWindowMap.get(str);
        if (metric != null) {
            try {
                metric.addEvent(event);
            } catch (Exception ex) {
                logger.error("metric {} add event fail", str, ex);
            }
        }

    }


    public void onConfigChange(String service, String method, CircuitBreakConfigMeta circuitBreakConfigMeta) {
        logger.info("configChange service  {} method {} {} ", service, method, circuitBreakConfigMeta);
        String key = service + delimiter + method;
        MetricTimeWindow metricTimeWindow = metricTimeWindowMap.get(key);
        if (metricTimeWindow != null) {
            metricTimeWindow.changeWindowLenth(circuitBreakConfigMeta.getSlideWindowInSeconds());
        }
    }


    public static void listen(MetricTimeWindow.CircuitBreakerEvent event) {
        EventType breakerEvent = null;
        switch (event.getEventType()) {
            case ClosedToOpen:
                breakerEvent = EventType.ON;
                break;
            case OpenToClosed:
                breakerEvent = EventType.OFF;
        }
        circuitBreakEventReport.report(event.getServiceName(), event.getMethod(), breakerEvent, event.getTime(), event.getReason());
    }

    public static class DegradeResult {
        DegradeStatus status;
        Object obj;

        public DegradeResult(DegradeStatus status) {
            this.status = status;
        }

        public DegradeStatus getStatus() {
            return status;
        }

        public void setStatus(DegradeStatus status) {
            this.status = status;
        }

        public Object getObj() {
            return obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }


        @Override
        public String toString() {
            return "DegradeResult [status=" + status + ", obj=" + obj + "]";
        }


    }

    public static enum DegradeStatus {
        // 降级     正常
        beDgraded, benormal, befallback
    }

    static class ReportRunnable implements Runnable {
        @Override
        public void run() {
            //  Auto-generated method stub
            try {
                logger.info("start report clientDegrade Status");
                Set<Map.Entry<String, MetricTimeWindow>> set = metricTimeWindowMap.entrySet();
                int size = metricTimeWindowMap.size();
                long time = System.currentTimeMillis();
                ArrayList<CircuitBreakMonitorData> reportData = new ArrayList<>(size + 10);
                Iterator<Map.Entry<String, MetricTimeWindow>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, MetricTimeWindow> entry = it.next();
                    MetricTimeWindow metircTimeWindow = entry.getValue();
                    CircuitBreakMonitorData data = new CircuitBreakMonitorData();
                    byte cruStatus = 1;
                    if (metircTimeWindow.getCircuitBreaker().isClose()) {
                        cruStatus = 0;
                    }
                    data.setStatus(cruStatus);
                    int[] windowData = metircTimeWindow.getMetircTimeWindowData();
                    int successNum = windowData[MetricEventType.success.ordinal()];
                    int failNum = windowData[MetricEventType.fail.ordinal()];
                    int timeoutNum = windowData[MetricEventType.timeout.ordinal()];
                    data.setFailCount(failNum);
                    data.setSuccessCount(successNum);
                    data.setTimeoutCount(timeoutNum);
                    data.setService(metircTimeWindow.getServiceName());
                    data.setMethod(metircTimeWindow.getMethod());
                    reportData.add(data);
                }
                if (reportData.size() > 0)
                    monitorDataReporter.report(time, reportData);
            } catch (Exception ex) {
                logger.info("report clientDegrade Status fail", ex);
            }
        }
    }


}
