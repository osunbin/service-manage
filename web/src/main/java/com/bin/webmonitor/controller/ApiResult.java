package com.bin.webmonitor.controller;

public class ApiResult<T> {
    private int code;//0 success other fails
    private String message;
    private String field;
    private T result;

    public ApiResult() {
    }


    public ApiResult(int code, String message, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public ApiResult(int code, String message, String field, T result) {
        this.code = code;
        this.message = message;
        this.field = field;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public ApiResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ApiResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getResult() {
        return result;
    }

    public ApiResult<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public String getField() {
        return field;
    }

    public ApiResult<T> setField(String field) {
        this.field = field;
        return this;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", field='" + field + '\'' +
                ", result=" + result +
                '}';
    }
}
