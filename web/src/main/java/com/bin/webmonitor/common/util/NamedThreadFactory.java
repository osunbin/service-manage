package com.bin.webmonitor.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    /**
     * 线程前缀
     */
    private final String prefix;

    /**
     * 线程池里线程的唯一编号，从1开始
     */
    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    /**
     * 是否daemon线程
     */
    private final boolean daemoThread;



    public NamedThreadFactory(String mark) {
        this(mark, false);
    }

    public NamedThreadFactory(String mark, boolean daemo) {
        this.prefix = String.format("arch-pThread-%s-", mark);
        daemoThread = daemo;

    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = prefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(runnable, name);
        ret.setDaemon(daemoThread);
        return ret;
    }
}
