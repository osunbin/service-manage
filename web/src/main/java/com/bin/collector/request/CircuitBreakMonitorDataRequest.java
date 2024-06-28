package com.bin.collector.request;

import com.bin.webmonitor.enums.ReportType;

import java.util.ArrayList;
import java.util.List;

public class CircuitBreakMonitorDataRequest extends BaseData {
    /**
     * 时间点(分钟级毫秒)
     */
    private long time;
    
    /**
     * 调用方
     */
    private String callerKey;
    
    /**
     * 熔断监控数据
     */
    private List<CircuitBreakMonitorData> circuitBreakMonitorDatas = new ArrayList<CircuitBreakMonitorData>();
    
    public CircuitBreakMonitorDataRequest() {
        this.reportType = ReportType.circuitBreakMonitorData;
    }
    
    public CircuitBreakMonitorDataRequest(long time, String callerKey, List<CircuitBreakMonitorData> circuitBreakMonitorDatas) {
        this.reportType = ReportType.circuitBreakMonitorData;
        this.time = time;
        this.callerKey = callerKey;
        this.circuitBreakMonitorDatas = circuitBreakMonitorDatas;
    }

    
    public static final int PACKAGE_MAX_SIZE = 200;
    
    /**
     * udp分包，如果大于65000则分包
     *
     * @return
     */
    public List<CircuitBreakMonitorDataRequest> split() {
        List<CircuitBreakMonitorDataRequest> res = new ArrayList<>();
        
        int size = 0, from = 0, to = 0;
        for (CircuitBreakMonitorData circuitBreakMonitorData : circuitBreakMonitorDatas) {
            size += circuitBreakMonitorData.toBytes().length;
            to++;
            
            if (size > PACKAGE_MAX_SIZE) { // 够一个包了
                res.add(new CircuitBreakMonitorDataRequest(this.time, this.callerKey, circuitBreakMonitorDatas.subList(from, to)));
                from = to;
                size = 0;
            }
        }
        
        if (from != to) { // 尾部的
            res.add(new CircuitBreakMonitorDataRequest(this.time, this.callerKey, circuitBreakMonitorDatas.subList(from, to)));
        }
        
        return res;
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
    
    public String getCallerKey() {
        return callerKey;
    }
    
    public void setCallerKey(String callerKey) {
        this.callerKey = callerKey;
    }
    
    public List<CircuitBreakMonitorData> getCircuitBreakMonitorDatas() {
        return circuitBreakMonitorDatas;
    }
    
    public void setCircuitBreakMonitorDatas(List<CircuitBreakMonitorData> circuitBreakMonitorDatas) {
        this.circuitBreakMonitorDatas = circuitBreakMonitorDatas;
    }
    
    @Override
    public String toString() {
        return "CircuitBreakMonitorDataRequest{" + "time=" + time + ", callerKey='" + callerKey + '\'' + ", circuitBreakMonitorDatas=" + circuitBreakMonitorDatas + '}';
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public CircuitBreakMonitorDataRequest toRequest(byte[] b) {
        return null;
    }

    /**
     *  熔断监控数据
     */
    public static class CircuitBreakMonitorData {
        /**
         * 服务
         */
        private String service;
        
        /**
         * 服务里的接口函数
         */
        private String method;
        
        /**
         * 成功数
         */
        private int successCount;
        
        /**
         * 失败数
         */
        private int failCount;
        
        /**
         * 超时数
         */
        private int timeoutCount;
        
        /**
         * 熔断器状态。0：关闭；1：打开
         */
        private byte status;
        
        public CircuitBreakMonitorData() {
        }
        
        public CircuitBreakMonitorData(String service, String method, int successCount, int failCount, int timeoutCount, byte status) {
            this.service = service;
            this.method = method;
            this.successCount = successCount;
            this.failCount = failCount;
            this.timeoutCount = timeoutCount;
            this.status = status;
        }


        public byte[] toBytes() {
            return new byte[]{};
        }
        
        public String getService() {
            return service;
        }
        
        public void setService(String service) {
            this.service = service;
        }
        
        public String getMethod() {
            return method;
        }
        
        public void setMethod(String method) {
            this.method = method;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }
        
        public int getFailCount() {
            return failCount;
        }
        
        public void setFailCount(int failCount) {
            this.failCount = failCount;
        }
        
        public int getTimeoutCount() {
            return timeoutCount;
        }
        
        public void setTimeoutCount(int timeoutCount) {
            this.timeoutCount = timeoutCount;
        }
        
        public byte getStatus() {
            return status;
        }
        
        public void setStatus(byte status) {
            this.status = status;
        }
        
        @Override
        public String toString() {
            return "CircuitBreakMonitorData{" + "service='" + service + '\'' + ", method='" + method + '\'' + ", successCount=" + successCount + ", failCount=" + failCount + ", timeoutCount=" + timeoutCount + ", status=" + status + '}';
        }
    }
}
