package com.bin.webmonitor.repository.domain;

/**
 *  流量信息对象
 */
public class Flux {
    
    private long id;
    
    /**
     * 收集器id
     */
    private int cid;
    
    /**
     * 流量值
     */
    private int value;
    
    /**
     * 所有耗时(含队列)
     */
    private long allcost;
    
    /**
     * 方法执行耗时(纯业务)
     */
    private long realcost;
    
    /**
     * 存储相对时间: MILLSECODS_2000;unix时间
     */
    private long mintime;
    
    public Flux() {
    }
    
    public Flux(long id, long cid, int value, long allcost, long realcost, long millSecond) {
        this.id = id;
        this.cid = (int)cid;
        this.value = value;
        this.allcost = allcost;
        this.realcost = realcost;
        this.mintime = millSecond;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public int getCid() {
        return cid;
    }
    
    public void setCid(int cid) {
        this.cid = cid;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public long getAllcost() {
        return allcost;
    }
    
    public void setAllcost(long allcost) {
        this.allcost = allcost;
    }
    
    public long getRealcost() {
        return realcost;
    }
    
    public void setRealcost(long realcost) {
        this.realcost = realcost;
    }
    
    public long getMintime() {
        return mintime;
    }
    
    public void setMintime(long mintime) {
        
        this.mintime = mintime;
    }
    
    public void sum(Flux flux) {
        this.allcost += flux.allcost;
        this.realcost += flux.realcost;
        this.value += flux.value;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Flux{");
        sb.append("id=").append(id);
        sb.append(", cid=").append(cid);
        sb.append(", value=").append(value);
        sb.append(", allcost=").append(allcost);
        sb.append(", realcost=").append(realcost);
        sb.append(", mintime=").append(mintime);
        sb.append('}');
        return sb.toString();
    }
}
