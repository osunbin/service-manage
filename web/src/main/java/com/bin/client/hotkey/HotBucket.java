package com.bin.client.hotkey;

import java.util.concurrent.atomic.AtomicInteger;

public class HotBucket {

    private final long windowStart;
    private AtomicInteger count;

    public HotBucket(long startTime) {
        this.windowStart = startTime;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void inc() {
        count.incrementAndGet();
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }
}
