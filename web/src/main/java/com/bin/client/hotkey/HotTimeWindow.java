package com.bin.client.hotkey;



import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *  本地计算,本地存储
 */
public class HotTimeWindow<V> {

    private HotBucketCircularArray bucketArray;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int bucketTimeSpan = 1000 ;// 1000ms
    private String hotKey;
    private V v;

    public HotTimeWindow(String hotKey) {
        this.hotKey =hotKey;
        this.bucketArray = new HotBucketCircularArray(10);
    }


    public void count() {
        HotBucket bucket = getCurrentBucket();
        bucket.inc();
    }


    HotBucket getCurrentBucket() {
        long currentTime = System.currentTimeMillis();
        lock.readLock().lock();
        HotBucket bucket = null;
        try{
            bucket = bucketArray.tail();
            if (bucket != null && currentTime <= (bucket.getWindowStart() + bucketTimeSpan))
                return bucket;
        }finally {
            lock.readLock().unlock();
        }
        boolean createNewBucket = false;
        lock.writeLock().lock();
        try {
            HotBucket check = bucketArray.tail();
            if (check != null && currentTime <= (check.getWindowStart() + bucketTimeSpan)) {
                bucket = check;

            } else {
                bucket = new HotBucket(currentTime);
                bucketArray.addBucket(bucket);
                createNewBucket = true;

            }
        } finally {
            lock.writeLock().unlock();
        }

        return bucket;
    }

}
