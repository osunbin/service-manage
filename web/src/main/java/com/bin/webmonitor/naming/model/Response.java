package com.bin.webmonitor.naming.model;

import com.bin.webmonitor.command.CommandException;
import com.bin.webmonitor.common.util.StringUtil;

import java.io.Serializable;

public class Response<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应码
     */
    private String code;
    
    /**
     * 响应信息
     */
    private String message;
    
    /**
     * 响应内容
     */
    private T data;
    
    public T checkAndReturn() {
        if (!"1000".equalsIgnoreCase(code)) {
            throw new CommandException(code, message);
        }
        return data;
    }
    
    public Response() {
    }
    
    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Response(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public boolean isSuccess() {
        return !StringUtil.isEmpty(code) && "1000".equals(code);
    }
    
    public String getCode() {
        return code;
    }
    
    public Response<T> setCode(String code) {
        this.code = code;
        return this;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Response<T> setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public T getData() {
        return data;
    }
    
    public Response<T> setData(T data) {
        this.data = data;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Response{");
        sb.append("code='").append(code).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
