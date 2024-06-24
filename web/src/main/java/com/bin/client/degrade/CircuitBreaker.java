package com.bin.client.degrade;

import com.bin.client.ClientService;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class CircuitBreaker {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
    private String circuitBreakerName;
    private final AtomicReference<Status> status = new AtomicReference<Status>(Status.CLOSED);
    private final AtomicLong circuitOpened = new AtomicLong(-1);
    private final AtomicInteger halfOpenPassNum = new AtomicInteger(0); // max 10;
    private static int maxHalfOpenPassNum = 10;
    private final ClientService clientService;
    private String serviceName ;
    private String method;
    public CircuitBreaker(String serviceName,String method,ClientService clientService){
        this.circuitBreakerName = serviceName + "@"+method;
        this.serviceName = serviceName;
        this.method = method;
        this.clientService = clientService;
    }

    /**
     * true 表示允许正常执行
     * @return
     */
    public boolean allowRequest(){
        CircuitBreakConfigMeta configData =  this.clientService.getCircuitBreakConfig(serviceName, method);
        if(configData == null || !configData.isEnabled())
            return true;
        if(configData.isForceClosed()){
            return true;
        }
        if(configData.isForceOpened()){
            return false;
        }

        if (circuitOpened.get() == -1) {
            return true;
        } else {
            if (status.get().equals(Status.HALF_OPEN)) {
                return false;
            } else {
                return isAfterSleepWindow(configData);
            }
        }

    }

    private boolean isAfterSleepWindow(CircuitBreakConfigMeta configData) {
        final long circuitOpenTime = circuitOpened.get();
        final long currentTime = System.currentTimeMillis();
        final long sleepWindowTime = configData.getSleepWindowInMilliseconds();
        return currentTime > circuitOpenTime + sleepWindowTime;
    }
    /**
     *
     * @return true 表示正常执行
     */
    public boolean attemptExecution() {
        CircuitBreakConfigMeta configData =  this.clientService.getCircuitBreakConfig(serviceName, method);
        if(configData == null || !configData.isEnabled())
            return true; // 不开启
        if(configData.isForceClosed()){
            return true; // 强制关闭
        }
        if(configData.isForceOpened()){
            return false; // 强制打开
        }

        if (circuitOpened.get() == -1) {
            return true; //  关闭状态
        } else {
            if (isAfterSleepWindow(configData)) {
                if (status.compareAndSet(Status.OPEN, Status.HALF_OPEN)) {
                    halfOpenPassNum.set(0);
                    return true;
                } else {
                    if(halfOpenPassNum.incrementAndGet() > maxHalfOpenPassNum)
                        return false;
                    else
                        return true;
                }
            } else {
                return false;
            }
        }
    }

    public boolean markSuccess() {
        boolean flag = false;
        if (status.compareAndSet(Status.HALF_OPEN, Status.CLOSED)) {
            circuitOpened.set(-1L);
            flag = true;
            //metrics.resetStream();
        }
        return flag;
    }
    //
    public void markNonSuccess() {
        if (status.compareAndSet(Status.HALF_OPEN, Status.OPEN)) {
            //This thread wins the race to re-open the circuit - it resets the start time for the sleep window
            logger.debug("circuitBreakerName {} reset to open",circuitBreakerName);
            circuitOpened.set(System.currentTimeMillis());
        }
    }


    public boolean tryMarkOpen(int errorPercentage,Long requestNum){
        CircuitBreakConfigMeta configData =  this.clientService.getCircuitBreakConfig(serviceName, method);
        // 强制关闭
        if(configData != null && !configData.isForceClosed() &&
                requestNum >= configData.getRequestVolumeThreshold() && errorPercentage >= configData.getErrorThresholdPercentage()){
            if (status.compareAndSet(Status.CLOSED, Status.OPEN)) {
                circuitOpened.set(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    public boolean isEnable(){
        CircuitBreakConfigMeta configData =  this.clientService.getCircuitBreakConfig(serviceName, method);
        if(configData != null && configData.isEnabled())
            return true;
        return false;
    }

    public Status getStatus(){
        return status.get();
    }

    public boolean isClose(){
        CircuitBreakConfigMeta configData =  this.clientService.getCircuitBreakConfig(serviceName, method);
        if(configData == null || !configData.isEnabled())
            return true;
        if(configData.isForceClosed()){
            return true;
        }
        if(configData.isForceOpened()){
            return false;
        }

        if (circuitOpened.get() == -1) {
            return true;
        }else
            return false;
    }

    @Override
    public String toString() {
        return "CircuitBreaker [circuitBreakerName=" + circuitBreakerName + ", status=" + status + ", circuitOpened="
                + circuitOpened + "]";
    }



    enum Status {
        CLOSED, OPEN, HALF_OPEN;
    }


}