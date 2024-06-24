package com.bin.client.hotkey;



import java.util.ArrayList;
import java.util.Arrays;

public class HotBucketCircularArray {
    private static final int DEFAULT_CAPACITY = 10;

    private volatile int size = DEFAULT_CAPACITY;
    private static int maxSize = 60;
    private volatile int dataLength;
    private HotBucket[] data;
    private int head;
    private int tail;


    public HotBucketCircularArray(int size) {
        data = new HotBucket[maxSize+1];
        head = 0;
        tail = 0;
        this.dataLength = 0;
        if(size > DEFAULT_CAPACITY && size <= maxSize)
            this.size = size;
    }

    public void addBucket(HotBucket bucket) {
        data[tail] = bucket;
        incrementTail();
    }

    public HotBucket tail() {
        if (dataLength == 0)
            return null;
        int index = (head + dataLength - 1) % dataLength;
        return data[index];
    }

    public HotBucket[] toArray() {

        ArrayList<HotBucket> list = new ArrayList<>();
        for (int i = 0; i < dataLength; i++) {
            int index = (head +i) % dataLength;
            // if the Array size expand , some case datalength is not right, by y
            HotBucket tmp = data[index];
            if(tmp != null)
                list.add(tmp);
        }
        return list.toArray(new HotBucket[list.size()]);
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
        data = new HotBucket[maxSize];
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
