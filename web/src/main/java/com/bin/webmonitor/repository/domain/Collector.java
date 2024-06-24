package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  数据收集对象
 */
public class Collector {
    
    private long id;
    
    /**
     * 收集对象名称(名称由前置业务决定，根据不同需求类型，整合业务字段，如：语言+服务名称+调用者名称+函数名称)
     */
    private String cname;
    
    private Date createtime;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getCname() {
        return cname;
    }
    
    public void setCname(String cname) {
        this.cname = cname;
    }
    
    public Date getCreatetime() {
        return createtime;
    }
    
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
    
    @Override
    public String toString() {
        return "Collector{" + "id=" + id + ", cname='" + cname + '\'' + ", createtime=" + createtime + '}';
    }
}
