package com.bin.webmonitor.component;

import com.bin.webmonitor.common.util.JsonHelper;

public class StrJson {

    @Override
    public String toString() {
        return JsonHelper.toJson(this);
    }
}
