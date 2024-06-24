package com.bin.webmonitor.model.vo;

import com.bin.webmonitor.component.StrJson;

public class Attributes extends StrJson {

    private String serialize;

    private String sips;

    public Attributes(String serialize, String sips) {
        this.serialize = serialize;
        this.sips = sips;
    }

    public String getSips() {
        return sips;
    }

    public void setSips(String sips) {
        this.sips = sips;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }
}
