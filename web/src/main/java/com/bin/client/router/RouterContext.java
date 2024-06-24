package com.bin.client.router;

import java.util.List;
import java.util.Map;

public class RouterContext {

    private String tags;
    private String methodName;

    private Object[] arguments;
    private List<Invoker> invokers;

    private Map<String,Object> attachment;


    public String getMethodName() {
        return methodName;
    }

    public String getTags() {
        return tags;
    }

    public List<Invoker> getInvokers() {
        return invokers;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getAttachment(String key) {
        Object o = attachment.get(key);
        if (o != null) return o.toString();
        return "";
    }


    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setInvokers(List<Invoker> invokers) {
        this.invokers = invokers;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

}
