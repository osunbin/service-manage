package com.bin.collector.request;

import com.bin.webmonitor.enums.ReportType;

public abstract class BaseData {

    /**
     * 请求类型
     */
    protected ReportType reportType;

    public abstract byte[] toBytes();


    public abstract BaseData toRequest(byte[] b);


    public ReportType getRequestType() {
        return this.reportType;
    }

}
