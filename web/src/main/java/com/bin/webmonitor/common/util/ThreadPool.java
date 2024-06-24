package com.bin.webmonitor.common.util;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(4, new NamedThreadFactory("sdk-schedule"));
    
    public static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(4, new NamedThreadFactory("sdk-executor"));
    
    /**
     * 仅仅在5-50 s调度，减小整点调度的可能性
     * 这个名字起的有问题，其实是scheduleAtFixedRate
     *
     * @param runnable
     * @param delay    milliseconds
     */
    public static void scheduleWithFixedDelay(Runnable runnable, long delay) {
        Random random = new Random();
        int second = random.nextInt(50) + 5;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        long initDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(runnable, initDelay, delay, TimeUnit.MILLISECONDS);
        
    }
    
}
