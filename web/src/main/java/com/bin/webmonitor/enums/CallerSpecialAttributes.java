package com.bin.webmonitor.enums;

/**
 *  调用方特殊节点属性类型：灰度调用的元数据
 */
public enum CallerSpecialAttributes {
    
    /**
     * 属性：序列化版本
     */
    SerializeVersion("serialize"), ServerIps("sips");
    
    private String key;
    
    private CallerSpecialAttributes(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return this.key;
    }
    
}
