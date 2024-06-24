package com.bin.webmonitor.model;

public class LabelValue {
    private String label; // 显示用
    private String value; // 取值用

    public String getLabel() {
        return label;
    }

    public LabelValue setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getValue() {
        return value;
    }

    public LabelValue setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LabelValue{");
        sb.append("label='").append(label).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
