package com.bin.webmonitor.externapi;

import com.bin.webmonitor.naming.model.Status;
import com.bin.webmonitor.naming.model.Statuses;

import java.util.Objects;

public class ServiceResult<T> {
    public static final Status SUCCESS = new Status(Statuses.SUCCESS, "success");
    
    public static final Status SERVER_ERROR = new Status(Statuses.SERVER_ERROR, "server error");
    
    /**
     * 状态
     */
    private Status status;
    
    /**
     * 结果
     */
    private T result;
    
    public ServiceResult(Status status) {
        this.status = status;
    }
    
    public ServiceResult(Status status, T result) {
        this.status = status;
        this.result = result;
    }

    public static ServiceResult noCaller(String callerKey) {
        return new ServiceResult<>(new Status(Statuses.NO_CALLER, "no caller by callerKey " + callerKey));
    }

    public static ServiceResult noService() {
        return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "服务不存在"), false);
    }

    public static ServiceResult noService(String serviceName) {
        return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "no service by name " + serviceName));
    }
    
    public Status getStatus() {
        return status;
    }
    
    public ServiceResult<T> setStatus(Status status) {
        this.status = status;
        return this;
    }
    
    public T getResult() {
        return result;
    }
    
    public ServiceResult<T> setResult(T result) {
        this.result = result;
        return this;
    }
    
    public boolean isSuccess() {
        return this.status.getCode() == SUCCESS.getCode();
    }
    
    public int getCode() {
        return this.status.getCode();
    }
    
    public String getMessage() {
        return this.status.getMessage();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceResult<?> that = (ServiceResult<?>)o;
        return Objects.equals(status, that.status) && Objects.equals(result, that.result);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(status, result);
    }
    
    @Override
    public String toString() {
        return "ServiceResult{" + "status=" + status + ", result=" + result + '}';
    }
}