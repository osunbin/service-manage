package com.bin.webmonitor.model;

public class ServerNode {
    /**
     * 节点名称
     */
    private String name;
    
    private String ip;
    
    private int port;
    
    /**
     * 版本
     */
    private String version;
    
    /**
     * 权重
     */
    private int weight;
    
    public ServerNode() {
    }
    
    public ServerNode(String name, String ip, int port, String version, int weight) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.version = version;
        this.weight = weight;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServerNode{");
        sb.append("name=").append(name);
        sb.append(",ip=").append(ip);
        sb.append(",port=").append(port);
        sb.append(",version=").append(version);
        sb.append(",weight=").append(weight);
        sb.append("}");
        return sb.toString();
    }
}
