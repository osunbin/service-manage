package com.bin.client.limiter;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;

/**
 *  sql限流
 */
public class RateLimiterManager {


    private ConcurrentHashMap<String, RateLimiter> funcLimiter = new ConcurrentHashMap<>();



    // spring aop 拦截package 这样可以动态修改拦截该package下那些方法
    public void register(String table,String function,double permitsPerSecond) {
        RateLimiter limiter = RateLimiter.create(permitsPerSecond);
        String key = table + "@" + function;
        funcLimiter.put(key,limiter);
    }


    public void isLimit(String table,String function) {
        String key = table + "@" + function;
        RateLimiter limiter = funcLimiter.get(key);

        if (!limiter.tryAcquire()) {
            // TODO 上报 ...
           throw new RuntimeException("table:" + table + " sql function:" + function + " rate limiter");
        }
    }
}
