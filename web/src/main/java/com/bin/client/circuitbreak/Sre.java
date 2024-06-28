package com.bin.client.circuitbreak;

import com.bin.client.internal.window.Bucket;
import com.bin.client.internal.window.RollingCounter;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Sre {
    private static final int StateOpen = 0;
    private static final int StateClosed = 1;

    private RollingCounter stat;
    private Random random;
    /**
     * 减小k将使自适应节流行为更加激进，增大k将使适应性节流行为不那么激进。
     */
    private double k;
    private long request;
    private AtomicInteger state;


    public Sre(Config config) {

        int bucket = config.bucket;
        long window = config.window;
        long bucketDuration = window / bucket;
        this.stat = new RollingCounter(bucket, bucketDuration);
        this.random = new Random(System.currentTimeMillis());
        this.request = config.request;
        this.k = 1 / config.success;
        this.state = new AtomicInteger(StateClosed);
    }


    // mark request is success
    public void markSuccess() {
        stat.add(1);
    }

    /**
     * mark request is failed
     * 当客户端在本地拒绝请求时，继续添加计数器让
     * 下降率更高
     */
    public void markFailed() {
        stat.add(0);
    }

    public boolean trueOnProba(double proba) {
        return Math.random() < proba;
    }


    public long[] summary() {
        long[] collect = new long[2];
        stat.reduce(lists -> {
            long total = 0;
            long success = 0;
            for (Bucket bucket : lists) {
                total += bucket.count();
                success += Math.round(Arrays.stream(bucket.points()).sum());
            }
            collect[0] = success;
            collect[1] = total;
            return 0;
        });
        return collect;
    }


    public boolean allow() {
        long[] summary = summary();
        long accepts = summary[0];
        long total = summary[1];

        double requests = k * (double) accepts;

        if (total < request || (double) total < requests) {
            state.compareAndExchange(StateOpen, StateClosed);
            return true;
        }
        state.compareAndExchange(StateClosed, StateOpen);
        double dr = Math.max(0, ((double) total - requests) / (double) (total + 1));
        boolean drop = trueOnProba(dr);
        if (drop) {
            return false;
        }
        return true;
    }


    public static class Config {
        double success = 0.6;
        long request = 100;
        int bucket = 10;
        long window = 3 * 1000; // 3s
    }
}
