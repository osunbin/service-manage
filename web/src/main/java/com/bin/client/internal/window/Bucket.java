package com.bin.client.internal.window;

import java.util.Arrays;

public class Bucket {
    private double[] points;
    private long count;
    private Bucket next;
    private int pos;

    public void append(double val) {
        points[pos++] = val;
        count++;
    }

    public void add(int offset,double val) {
        points[offset] += val;
        count++;
    }



    public void reset() {
        for (int i =0;i <pos;i++) {
            points[i] = 0;
        }
        count = 0;
    }

    public Bucket next() {
        return next;
    }

    public void setNext(Bucket bucket) {
        next = bucket;
    }


    public long count() {
        return count;
    }

    public double[] points() {
        return points;
    }
}
