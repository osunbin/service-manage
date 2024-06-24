package com.bin.client.degrade;

import com.bin.client.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MetricTimeWindow {

    private static Logger logger = LoggerFactory.getLogger(MetricTimeWindow.class);
    private BucketCircularArray bucketArray;
    private int bucketTimeSpan = 1000 ;// 1000ms
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private String metricName;
    private final CircuitBreaker circuitBreaker;
    private final String serviceName;
    private final String method;
    private final CircuitBreakerListener circuitBreakerListener;
    public MetricTimeWindow(String serviceName, String method, ClientService clientService, int windowLength, CircuitBreakerListener circuitBreakerListener) {
        this.metricName = serviceName + "@" + method;
        this.serviceName = serviceName;
        this.method = method;
        this.bucketArray = new BucketCircularArray(windowLength);
        this.circuitBreaker = new CircuitBreaker(serviceName, method, clientService);
        this.circuitBreakerListener = circuitBreakerListener;
    }

    public void addEvent(MetricEventType event) {
        Bucket bucket = getCurrentBucket();
        switch(event){
            case success:
                bucket.getSuccessNum().incrementAndGet();
                boolean makeClosed = circuitBreaker.markSuccess();
                if(makeClosed){
                    logger.info("metricName {} circuitBreaker change to closed ",metricName);
                    CircuitBreakerEvent changeEvent = new CircuitBreakerEvent();
                    changeEvent.setServiceName(serviceName);
                    changeEvent.setMethod(method);
                    changeEvent.setTime(System.currentTimeMillis());
                    changeEvent.setEventType(CircuitBreakerEventType.OpenToClosed);
                    String reason = "open TO closed";
                    changeEvent.setReason(reason);
                    circuitBreakerListener.listen(changeEvent);
                    resetBucketArray();
                }
                break;
            case fail:
                bucket.getFailNum().incrementAndGet();
                this.circuitBreaker.markNonSuccess();
                break;
            case timeout:
                bucket.getTimeoutNum().incrementAndGet();
                this.circuitBreaker.markNonSuccess();
                break;
        }
    }

    private void resetBucketArray() {
        lock.writeLock().lock();
        try {
            this.bucketArray.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    Bucket getCurrentBucket() {
        long currentTime = System.currentTimeMillis();
        lock.readLock().lock();
        Bucket bucket = null;
        try{
            bucket = bucketArray.tail();
            if (bucket != null && currentTime <= (bucket.getWindowStart() + bucketTimeSpan))
                return bucket;
        }finally {
            lock.readLock().unlock();
        }
        boolean createNewBucket = false;
        lock.writeLock().lock();
        try {
            Bucket check = bucketArray.tail();
            if (check != null && currentTime <= (check.getWindowStart() + bucketTimeSpan)) {
                bucket = check;
                // if someone have create the bucket before (hold the write lock )
            } else {
                bucket = new Bucket(currentTime);
                bucketArray.addBucket(bucket);
                createNewBucket = true;
                //logger.info("bucket create "+bucket);
            }
        } finally {
            lock.writeLock().unlock();
        }
        if(createNewBucket)
            dealCreateBucketEvent();

        return bucket;
    }

    private void dealCreateBucketEvent(){
        // create new bucket,calculate the error %,update CircuitBreaker
        Bucket[] data = null;
        lock.readLock().lock();
        try {
            data = bucketArray.toArray();
        } finally {
            lock.readLock().unlock();
        }
        if(data == null || data.length == 0)
            return ;
        Long successNum = 0L;
        Long failNum = 0L;

        for(int i=0;i<data.length;i++){
            Bucket tmp = data[i];
            successNum  += tmp.getSuccessNum().get();
            failNum += tmp.getFailNum().get();
            failNum += tmp.getTimeoutNum().get();
        }
        Long reqNum = successNum + failNum;
        int errorPercentage = (int) ((double) failNum / (successNum + failNum)* 100 );
        boolean changeToOpen = circuitBreaker.tryMarkOpen(errorPercentage, reqNum);
        if(changeToOpen){
            logger.info("metricName {} circuitBreaker change to open ",metricName);
            CircuitBreakerEvent changeEvent = new CircuitBreakerEvent();
            changeEvent.setServiceName(serviceName);
            changeEvent.setMethod(method);
            changeEvent.setTime(System.currentTimeMillis());
            changeEvent.setEventType(CircuitBreakerEventType.ClosedToOpen);
            String reason = String.format("closedToOpen,in window reqNum %s errorPercentage %s",reqNum,errorPercentage);
            changeEvent.setReason(reason);
            circuitBreakerListener.listen(changeEvent);
        }

    }

    public int[] getMetircTimeWindowData(){
        Bucket[] data = null;
        lock.readLock().lock();
        try {
            data = bucketArray.toArray();
        } finally {
            lock.readLock().unlock();
        }
        int[] windowData = new int[3];
        for(int i=0;i<3;i++)
            windowData[i] = 0;

        if(data == null || data.length == 0)
            return windowData;
        int successNum = 0;
        int failNum = 0;
        int timeoutNum = 0;
        for(int i=0;i<data.length;i++){
            Bucket tmp = data[i];
            successNum  += tmp.getSuccessNum().get();
            failNum += tmp.getFailNum().get();
            timeoutNum += tmp.getTimeoutNum().get();
        }

        windowData[MetricEventType.success.ordinal()] = successNum ;
        windowData[MetricEventType.fail.ordinal()] = failNum ;
        windowData[MetricEventType.timeout.ordinal()] = timeoutNum;
        return windowData;
    }

    public void changeWindowLenth(int slideWindowInSeconds) {
        if(slideWindowInSeconds >= 10 && slideWindowInSeconds <= 60){
            int cruSize = bucketArray.getSize();
            if(cruSize != slideWindowInSeconds)
                logger.info("changeWindowLength {}",slideWindowInSeconds);
            if(slideWindowInSeconds > cruSize)
                bucketArray.expand(slideWindowInSeconds);
            if(slideWindowInSeconds < cruSize){
                lock.writeLock().lock();
                try{
                    bucketArray.shrink(slideWindowInSeconds);
                }catch(Exception ex){
                    logger.info("changeWindowLenth error",ex);
                }finally {
                    lock.writeLock().unlock();
                }
            }

        }
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    @Override
    public String toString() {
        return "MetricTimeWindow [metricName=" + metricName + "]";
    }



    public String getServiceName() {
        return serviceName;
    }

    public String getMethod() {
        return method;
    }



    public static enum MetricEventType {
        success, fail, timeout
    }

    static enum CircuitBreakerEventType {
        ClosedToOpen,OpenToClosed
    }
    static class CircuitBreakerEvent {
        private String serviceName;
        private String method;
        private CircuitBreakerEventType eventType;
        private String reason;
        private long time;
        public String getServiceName() {
            return serviceName;
        }
        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
        public String getMethod() {
            return method;
        }
        public void setMethod(String method) {
            this.method = method;
        }
        public CircuitBreakerEventType getEventType() {
            return eventType;
        }
        public void setEventType(CircuitBreakerEventType eventType) {
            this.eventType = eventType;
        }
        public String getReason() {
            return reason;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }
        public long getTime() {
            return time;
        }
        public void setTime(long time) {
            this.time = time;
        }
        @Override
        public String toString() {
            return "CircuitBreakerEvent [serviceName=" + serviceName + ", method=" + method + ", eventType=" + eventType
                    + ", reason=" + reason + ", time=" + time + "]";
        }

    }

}
