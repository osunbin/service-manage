package com.bin.client.degrade;

import java.util.ArrayList;
import java.util.Arrays;

public class BucketCircularArray {

    private static final int DEFAULT_CAPACITY = 10;

    private volatile int size = DEFAULT_CAPACITY;
    private static int maxSize = 60;
    private volatile int dataLength;
    private Bucket[] data;
    private int head;
    private int tail;


    public BucketCircularArray(int size) {
        data = new Bucket[maxSize+1];
        head = 0;
        tail = 0;
        this.dataLength = 0;
        if(size > DEFAULT_CAPACITY && size <= maxSize)
            this.size = size;
    }

    public void addBucket(Bucket bucket) {
        data[tail] = bucket;
        incrementTail();
    }

    public Bucket tail() {
        if (dataLength == 0)
            return null;
        int index = (head + dataLength - 1) % dataLength;
        return data[index];
    }

    public Bucket[] toArray() {

//        Bucket[] buckets = new Bucket[dataLength];
//        for (int i = 0; i < dataLength; i++) {
//            int index = (head +i) % dataLength;
//            buckets[i] = data[index];
//        }
//        return buckets;

        ArrayList<Bucket> list = new ArrayList<Bucket>();
        for (int i = 0; i < dataLength; i++) {
            int index = (head +i) % dataLength;
            // if the Array size expand , some case datalength is not right, by y
            Bucket tmp = data[index];
            if(tmp != null)
                list.add(tmp);
        }
        return list.toArray(new Bucket[list.size()]);
    }

    private void incrementTail() {
        if (dataLength == size || (dataLength != 0 && head == tail) ) {
            // the size change
            head = (head + 1) % size;
            tail = (tail + 1) % size;
        } else
            tail = (tail + 1) % size;
        if (dataLength < size)
            dataLength++;
    }

    public void expand(int size) {
        if (size < DEFAULT_CAPACITY || size > maxSize)
            return ;
        if(size > this.size)
            this.size = size;
    }

    public void shrink(int size){
        if(size < DEFAULT_CAPACITY || size > maxSize)
            return ;
        if(size < this.size){
            this.size = size;
            this.dataLength = 0;
            this.head = 0;
            this.tail = 0;
        }
    }



    public int getSize() {
        return size;
    }


    public void clear() {
        data = new Bucket[maxSize];
        this.head = 0;
        this.dataLength = 0;
        this.tail = 0;
    }

    @Override
    public String toString() {
        return "BucketCircularArray [size=" + size + ", dataLength=" + dataLength + ", data=" + Arrays.toString(data)
                + ", head=" + head + ", tail=" + tail + "]";
    }

}