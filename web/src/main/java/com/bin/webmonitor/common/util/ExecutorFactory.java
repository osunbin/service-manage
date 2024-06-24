package com.bin.webmonitor.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorFactory {

    private static final int N_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_POOL_SIZE = 500;
    private static final int CAPACITY = 1024;
    private static final long KEEP_ALIVE_TIME = 0L;

    /**
     * 常用的线程池.
     *
     * @param poolName 池名
     */
    public static ExecutorService newDefault(String poolName) {
        return newByCorePoolSize(poolName, N_THREADS);
    }

    /**
     * 指定线程池大小.
     *
     * @param poolName 池名
     */
    public static ExecutorService newByCorePoolSize(String poolName, int corePoolSize) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(
            poolName + "-pool-%d").build();
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(CAPACITY);
        ThreadPoolExecutor.AbortPolicy handler = new ThreadPoolExecutor.AbortPolicy();
        return new ThreadPoolExecutor(corePoolSize, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS, workQueue, threadFactory, handler);
    }

    /**
     * 定时调度线程池.
     *
     * @param poolName 池名
     * @param poolSize 池大小
     */
    public static ScheduledExecutorService newScheduled(int poolSize, String poolName) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(
            poolName + "-pool-%d").build();
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory);
    }

}
