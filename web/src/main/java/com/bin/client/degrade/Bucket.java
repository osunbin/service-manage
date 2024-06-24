package com.bin.client.degrade;

import java.util.concurrent.atomic.AtomicInteger;

public class Bucket {

    private final long windowStart;
    private AtomicInteger successNum;
    private AtomicInteger failNum;
    private AtomicInteger timeoutNum;

    public Bucket(long startTime){
        this.windowStart = startTime;
        this.successNum = new AtomicInteger(0);
        this.failNum = new AtomicInteger(0);
        this.timeoutNum = new AtomicInteger(0);
    }

    public AtomicInteger getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(AtomicInteger successNum) {
        this.successNum = successNum;
    }

    public AtomicInteger getFailNum() {
        return failNum;
    }

    public void setFailNum(AtomicInteger failNum) {
        this.failNum = failNum;
    }

    public AtomicInteger getTimeoutNum() {
        return timeoutNum;
    }

    public void setTimeoutNum(AtomicInteger timeoutNum) {
        this.timeoutNum = timeoutNum;
    }

    public long getWindowStart() {
        return windowStart;
    }

    @Override
    public String toString() {
        return "Bucket [windowStart=" + windowStart + "]";
    }


    public static void main(String[] args) {

    }

}
