package com.bin.webmonitor.model;

import java.util.Map;
import java.util.Objects;

public class ServerConfig {
    /**
     * 配置内容
     */
    private String config;
    
    /**
     * 变更时间
     */
    private long changeTime;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> ext;
    
    public ServerConfig(String config, Map<String, Object> ext) {
        this.config = config;
        this.ext = ext;
    }
    
    public long getChangeTime() {
        return changeTime;
    }
    
    public ServerConfig setChangeTime(long changeTime) {
        this.changeTime = changeTime;
        return this;
    }


    public String getConfig() {
        return config;
    }

    public ServerConfig setConfig(String config) {
        this.config = config;
        return this;
    }

    public Map<String, Object> getExt() {
        return ext;
    }
    
    public ServerConfig setExt(Map<String, Object> ext) {
        this.ext = ext;
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerConfig that = (ServerConfig)o;
        return changeTime == that.changeTime && Objects.equals(config, that.config) && Objects.equals(ext, that.ext);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(config, changeTime, ext);
    }
    
    @Override
    public String toString() {
        return "ServerConfig{" + "config='" + config + '\'' + ", changeTime=" + changeTime + ", ext=" + ext + '}';
    }
}