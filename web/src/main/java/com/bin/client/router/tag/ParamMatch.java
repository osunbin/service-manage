package com.bin.client.router.tag;

public class ParamMatch {
    private String key;
    private StringMatch value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public StringMatch getValue() {
        return value;
    }

    public void setValue(StringMatch value) {
        this.value = value;
    }

    public boolean isMatch(String input) {
        if (getValue() != null) {
            return getValue().isMatch(input);
        }
        return false;
    }
}
