package com.bin.webmonitor.model.dto;

import com.bin.webmonitor.component.StrJson;

public class CallerSpecialDto extends StrJson {

    private Integer id;

   // @NotNull(message = "调用方id不能为空")
    private Integer cid;

  //  @NotNull(message = "服务方id不能为空")
    private Integer sid;

  //  @NotEmpty(message = "调用方ip列表不能为空")
    private String cips;

   // @NotEmpty(message = "serialize不能为空")
    private String serialize;

  //  @NotEmpty(message = "至少要选择一个服务方ip")
    private String sips;

    private String callerName;

    private String serviceName;

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

    public String getCips() {
        return cips;
    }

    public void setCips(String cips) {
        this.cips = cips;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public String getSips() {
        return sips;
    }

    public void setSips(String sips) {
        this.sips = sips;
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


}