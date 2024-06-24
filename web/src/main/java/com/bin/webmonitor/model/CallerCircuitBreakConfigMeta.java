package com.bin.webmonitor.model;

import java.util.HashMap;
import java.util.Map;

public class CallerCircuitBreakConfigMeta {
    /**
     * k-v:service-method
     */
    private Map<String, Map<String, CircuitBreakConfigMeta>> serviceMethodConfigMap = new HashMap<String, Map<String, CircuitBreakConfigMeta>>();
    
    public CallerCircuitBreakConfigMeta() {
    }
    
    public CallerCircuitBreakConfigMeta(Map<String, Map<String, CircuitBreakConfigMeta>> serviceMethodConfigMap) {
        this.serviceMethodConfigMap = serviceMethodConfigMap;
    }
    
    public Map<String, CircuitBreakConfigMeta> getMethod2CircuitBreakConfigMap(String service) {
        return serviceMethodConfigMap.get(service);
    }
    
    public CircuitBreakConfigMeta getCircuitBreakConfig(String service, String method) {
        if (this.serviceMethodConfigMap.containsKey(service)) {
            return this.serviceMethodConfigMap.get(service).get(method);
        }
        return null;
    }
    
    public Map<String, Map<String, CircuitBreakConfigMeta>> getServiceMethodConfigMap() {
        return serviceMethodConfigMap;
    }
    
    public void setServiceMethodConfigMap(Map<String, Map<String, CircuitBreakConfigMeta>> serviceMethodConfigMap) {
        this.serviceMethodConfigMap = serviceMethodConfigMap;
    }
    
    @Override
    public String toString() {
        return "CallerCircuitBreakConfigMeta{" + "serviceMethodConfigMap=" + serviceMethodConfigMap + '}';
    }
    
    /**
     * 熔断配置元数据对象
     */
    public static class CircuitBreakConfigMeta {
        /**
         * 是否开启熔断机制。0：不是；1：是。
         */
        private boolean enabled;
        
        /**
         * 是否强制开启熔断机制。0：不是；1：是。
         */
        private boolean forceOpened;
        
        /**
         * 是否强制关闭熔断机制。0：不是；1：是。
         */
        private boolean forceClosed;
        
        /**
         * 滑动窗口时长（秒)
         */
        private int slideWindowInSeconds;
        
        /**
         * 窗口内进行熔断判断的最小请求个数（请求数过少则不会启动熔断机制）
         */
        private int requestVolumeThreshold;
        
        /**
         * 错误比例，百分比，取值范围为[0,100]
         */
        private int errorThresholdPercentage;
        
        /**
         * 熔断时间窗口时长（毫秒)
         */
        private int sleepWindowInMilliseconds;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public boolean isForceOpened() {
            return forceOpened;
        }
        
        public void setForceOpened(boolean forceOpened) {
            this.forceOpened = forceOpened;
        }
        
        public boolean isForceClosed() {
            return forceClosed;
        }
        
        public void setForceClosed(boolean forceClosed) {
            this.forceClosed = forceClosed;
        }
        
        public int getSlideWindowInSeconds() {
            return slideWindowInSeconds;
        }
        
        public void setSlideWindowInSeconds(int slideWindowInSeconds) {
            this.slideWindowInSeconds = slideWindowInSeconds;
        }
        
        public int getRequestVolumeThreshold() {
            return requestVolumeThreshold;
        }
        
        public void setRequestVolumeThreshold(int requestVolumeThreshold) {
            this.requestVolumeThreshold = requestVolumeThreshold;
        }
        
        public int getErrorThresholdPercentage() {
            return errorThresholdPercentage;
        }
        
        public void setErrorThresholdPercentage(int errorThresholdPercentage) {
            this.errorThresholdPercentage = errorThresholdPercentage;
        }
        
        public int getSleepWindowInMilliseconds() {
            return sleepWindowInMilliseconds;
        }
        
        public void setSleepWindowInMilliseconds(int sleepWindowInMilliseconds) {
            this.sleepWindowInMilliseconds = sleepWindowInMilliseconds;
        }
        
        @Override
        public String toString() {
            return "CircuitBreakConfigMeta{" + "enabled=" + enabled + ", forceOpened=" + forceOpened + ", forceClosed=" + forceClosed + ", slideWindowInSeconds=" + slideWindowInSeconds + ", requestVolumeThreshold=" + requestVolumeThreshold + ", errorThresholdPercentage=" + errorThresholdPercentage
                + ", sleepWindowInMilliseconds=" + sleepWindowInMilliseconds + '}';
        }
    }
}
