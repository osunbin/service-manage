package com.bin.webmonitor.common;

import java.util.stream.Stream;

public enum BaseError {

    // 常用 Error Code
    SUCCESS(0, "成功", "success", ""),
    UNKNOWN(-1, "网络异常,请稍后重试", "Network error, please try later.", "網絡異常，請稍後重試"),
    REQ_INVALID(-2, "请求无效", "request is invalid.", ""),
    PARAM_OVERFLOW(-3, "参数值溢出", "request param threshold overflow.", ""),
    PARAM_WRONG(2, "参数错误", "Param wrong", ""),
    SERVICE_ERROR(3, "服务异常", "Service error", ""),
    IO_ERROR(4, "网络传输异常", "IO exception", ""),
    TIME_OUT(5, "请求超时, 请稍后重试", "Time out exception", ""),
    APP_KEY_ILLEGAL(6, "非法的app key", "llegal app key", ""),
    CONDITION_ILLEGAL(7, "不满足执行条件", "Not satisfied with the execution conditions.", ""),
    NOT_LOGIN(9, "用户未登陆", "Not login", ""),
    NO_PERMISSION(14, "用户无操作权限", "User no permission", ""),
    PROCESS_OVER(20, "流程已结束", "The process is over", ""),

    // -4XX Database fail
    FAIL_DB_OPERATION(-401, "数据库访问失败", "Internal error", "數據庫訪問失敗"),
    EMAIL_NOT_RECEIVED(-402, "邮件还未收到，请稍后重试", "Email not received, please try later",
        "郵件還未收到，請稍後重試"),
    DUPLICATE_KEY(-499, "数据已存在, 请勿重复添加.", "Duplicate key exceptio error", ""),
    ID_NOT_EXIST(-498, "记录不存在", "record no exist", ""),
    ALREADY_UPDATE(-497, "请检查数据是否存在或已更新", "record already update", ""),

    // 5XX Server error
    INTERNAL_SERVER_ERROR(500, "内部服务异常, 请稍后重试", "Internal server error", "Internal server error"),
    REQUEST_PROCESSING(
        501, "请求响应中，请勿重新提交", "Request is processing, please don't resubmit", "请求响应中，请勿重新提交"),


    SUB_SESSION_SCOPE_ERROR(1201, "访问受限", "Visit restricted", "訪問受限"),

    ILLEGAL_KEY(58001, "非法秘钥", "Unauthorised secret key", ""),

    MESSAGE_ERROR(58002, "消息投递异常", "message delivery error", "");

    private int code;

    private String message;

    @SuppressWarnings("unused")
    private String enMessage;

    @SuppressWarnings("unused")
    private String chtMessage;

    BaseError(int code, String message, String enMessage, String chtMessage) {
        this.code = code;
        this.message = message;
        this.enMessage = enMessage;
        this.chtMessage = chtMessage;
    }

    /**
     * code -> BaseError.
     */
    public static BaseError codeof(int code) {
        return Stream.of(BaseError.values()).filter(type -> type.code() == code).findFirst()
            .orElse(UNKNOWN);
    }

    public int code() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
