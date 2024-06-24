package com.bin.webmonitor.command;

import com.bin.webmonitor.common.util.JsonHelper;

public abstract class BaseCommand {
    /**
     * 指令编号
     */
    protected int code;
    
    /**
     * 获取指令内容
     */
    public String toContent() {
        return code + JsonHelper.toJson(this);
    }
    
    public int getCode() {
        return code;
    }
    
    public BaseCommand setCode(int code) {
        this.code = code;
        return this;
    }
}
