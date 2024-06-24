package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.BaseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

import static com.bin.webmonitor.common.BaseError.DUPLICATE_KEY;
import static com.bin.webmonitor.common.BaseError.INTERNAL_SERVER_ERROR;
import static com.bin.webmonitor.common.BaseError.IO_ERROR;
import static com.bin.webmonitor.common.BaseError.PARAM_WRONG;
import static com.bin.webmonitor.common.BaseError.SUCCESS;
import static com.bin.webmonitor.common.BaseError.TIME_OUT;

public class BaseController {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String EMPTY_STRING = "";

    @SuppressWarnings("unchecked")
    protected <T> ApiResult<T> okJson() {
        return (ApiResult<T>) okJson(EMPTY_STRING);
    }

    protected <T> ApiResult<T> okJson(Object value) {
        return (ApiResult<T>) okJson(EMPTY_STRING, EMPTY_STRING, value);
    }

    protected <T> ApiResult<T> okJson(T value, String message) {
        return okJson(message, EMPTY_STRING, value);
    }

    protected <T> ApiResult<T> okJson(String message, String filed, T value) {
        return okJson(SUCCESS.code(), message, filed, value);
    }

    protected <T> ApiResult<T> errorJson(String message) {
        return errorJson(INTERNAL_SERVER_ERROR.code(), message);
    }

    protected <T> ApiResult<T> errorJson(String message, T result) {
        return new ApiResult<>(INTERNAL_SERVER_ERROR.code(), message, "", result);
    }

    protected <T> ApiResult<T> errorJson(int errCode, String errMsg) {
        return errorJson(errCode, errMsg, "");
    }

    @SuppressWarnings("unchecked")
    protected <T> ApiResult<T> errorJson(int errCode, String errMsg, String filed) {
        return (ApiResult<T>) okJson(errCode, errMsg, filed, "");
    }

    protected <T> ApiResult<T> okJson(int errCode, String errMsg, String filed, T result) {
        return new ApiResult<>(errCode, errMsg, filed, result);
    }

    @ExceptionHandler(Exception.class)
    protected Object exceptionHandler(Exception ex) {
        String simpleName = ex.getClass().getSimpleName();
        String errMsg = ex.getMessage();

        log.warn("exceptionHandler name:{} msg:{}", simpleName, errMsg, ex);
        switch (simpleName) {
            case "IllegalArgumentException":

                return errorJson(PARAM_WRONG.code(),
                        Optional.of(errMsg).orElse(PARAM_WRONG.getMessage()));
            case "DataAccessException":
                BaseError failDbOperation = BaseError.FAIL_DB_OPERATION;
                return errorJson(failDbOperation.code(), failDbOperation.getMessage());
            case "DuplicateKeyException":
                return errorJson(DUPLICATE_KEY.code(), DUPLICATE_KEY.getMessage());
            case "IOException":
                return errorJson(IO_ERROR.code(), IO_ERROR.getMessage());
            case "HttpException":
                return errorJson(BaseError.UNKNOWN.code(), BaseError.UNKNOWN.getMessage());
            case "TimeoutException":
                return errorJson(TIME_OUT.code(), TIME_OUT.getMessage());
            case "UnsupportedEncodingException":
                return errorJson(TIME_OUT.code(), TIME_OUT.getMessage());
            case "RuntimeException":
                return errorJson(BaseError.UNKNOWN.code(), errMsg);
            default:
                return errorJson(INTERNAL_SERVER_ERROR.code(), errMsg);
        }
    }

}
