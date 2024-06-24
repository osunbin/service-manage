package com.bin.webmonitor.environment;

import java.util.HashMap;
import java.util.Map;

public enum EnvironmentEnum {
    UNKNOWN("unknown", "未知"), DEV("dev", "开发"), TEST("test", "测试"), PROD("prod", "线上");

    private String code;
    private String desc;

    private EnvironmentEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<String, EnvironmentEnum> codeMap =
            new HashMap<>(EnvironmentEnum.values().length);
    static {
        for (EnvironmentEnum environmentEnum : EnvironmentEnum.values()) {
            codeMap.put(environmentEnum.getCode(), environmentEnum);
        }
    }

    public static EnvironmentEnum codeOf(String code) {
        return codeMap.getOrDefault(code, UNKNOWN);
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
