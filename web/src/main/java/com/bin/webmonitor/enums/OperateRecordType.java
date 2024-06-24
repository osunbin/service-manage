package com.bin.webmonitor.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperateRecordType {
    UNKNOWN(0, "未知"),


    CALLER_REGISTER(1011, "调用方注册"),
    CALLER_UPDATE(1012, "调用方更新"),
    CALLER_DELETE(1013, "调用方删除"),
    CALLER_ALARM_ADD(1021, "调用方增加告警设置"),
    CALLER_ALARM_UPDATE(1022, "调用方修改告警设置"),
    CALLER_ALARM_DELETE(1023, "调用方删除告警设置"),
    CALLER_GRAY_ADD(1031, "调用方增加灰度配置"),
    CALLER_GRAY_UPDATE(1032, "调用方修改灰度配置"),
    CALLER_GRAY_DELETE(1033, "调用方删除灰度配置"),
    CALLER_SERVICEGROUP_UPDATE(1041, "通知调用方服务分组更新"),

    SERVICE_REGISTER(2011, "服务方注册"),
    SERVICE_UPDATE(2012, "服务方更新"),
    SERVICE_DELETE(2013, "服务方删除"),
    SERVICE_FUNCTION_ADD(2021, "服务方增加函数"),
    SERVICE_FUNCTION_UPDATE(2022, "服务方更新函数"),
    SERVICE_FUNCTION_DELETE(2023, "服务方删除函数"),
    SERVICE_FUNCTIONGROUP_ADD(2031, "服务方增加函数分组"),
    SERVICE_FUNCTIONGROUP_UPDATE(2032, "服务方更新函数分组"),
    SERVICE_FUNCTIONGROUP_DELETE(2033, "服务方删除函数分组"),
    SERVICE_NODEGROUP_ADD(2041, "服务方增加节点分组"),
    SERVICE_NODEGROUP_UPDATE(2042, "服务方更新节点分组"),
    SERVICE_NODEGROUP_DELETE(2043, "服务方删除节点分组"),
    SERVICE_ALARM_ADD(2051, "服务方增加告警设置"),
    SERVICE_ALARM_UPDATE(2052, "服务方更新告警设置"),
    SERVICE_ALARM_DELETE(2053, "服务方删除告警设置"),
    SERVICE_NODE_ADD(2061, "服务方增加节点"),
    SERVICE_NODE_UPDATE(2062, "服务方修改节点"),
    SERVICE_NODE_DELETE(2063, "服务方删除节点"),
    SERVICE_CALLER_REJECT(2071, "服务方拒绝调用方调用"),

    CALLERUSAGE_REGISTER_APPLY(3011, "新建调用关系申请"),
    CALLERUSAGE_REGISTER_PASS(3012, "新建调用关系通过"),
    CALLERUSAGE_UPDATE_APPLY(3021, "更新调用关系申请"),
    CALLERUSAGE_UPDATE_PASS(3022, "更新调用关系通过"),
    CALLERUSAGE_DELETE_APPLY(3031, "删除调用关系申请"),
    CALLERUSAGE_DELETE_PASS(3032, "删除调用关系通过"),
    CALLERUSAGE_FUNCTION_ADD(3041, "调用关系增加函数调用"),
    CALLERUSAGE_FUNCTION_UPDATE(3042, "调用关系修改函数调用"),
    CALLERUSAGE_FUNCTION_DELETE(3043, "调用关系函数删除"),
    CALLERUSAGE_FUNCTION_DEGRADE(3044, "调用关系函数降级"),
    CALLERUSAGE_SERVICE_DEGRADE(3051, "调用关系服务方降级");


    private static Map<Integer, OperateRecordType> codeMap = new HashMap<>(OperateRecordType.values().length);

    int code;
    String desc;

    OperateRecordType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    static {
        for (OperateRecordType myEnum : OperateRecordType.values()) {
            codeMap.put(myEnum.getCode(), myEnum);
        }
    }

    public static OperateRecordType codeOf(final int code) {
        return codeMap.getOrDefault(code, OperateRecordType.UNKNOWN);
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
