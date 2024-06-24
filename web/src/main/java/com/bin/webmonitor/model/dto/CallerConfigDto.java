package com.bin.webmonitor.model.dto;

import com.bin.webmonitor.component.StrJson;

public class CallerConfigDto extends StrJson {

    private Integer id;

   // 调用方id
    private Integer cid;

    private String callerName;


    private Integer sid;

    private String serviceName;

    private Integer gid;

    private String groupName;


    /**
     * 调用描述
     */
    private String description;




    /**
     * 调用粒度。0函数级；1服务级。
     */
    private Integer granularity;

    /**
     * 函数调用量列表
     */
    private String funcConfigsJson;

    /**
     * 函数超时列表
     */
    private String functionTimeoutJson;








    public CallerConfigDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getGranularity() {
        return granularity;
    }

    public void setGranularity(Integer granularity) {
        this.granularity = granularity;
    }

    public String getFuncConfigsJson() {
        return funcConfigsJson;
    }

    public void setFuncConfigsJson(String funcConfigsJson) {
        this.funcConfigsJson = funcConfigsJson;
    }

    public String getFunctionTimeoutJson() {
        return functionTimeoutJson;
    }

    public void setFunctionTimeoutJson(String functionTimeoutJson) {
        this.functionTimeoutJson = functionTimeoutJson;
    }




    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }



    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }





}
