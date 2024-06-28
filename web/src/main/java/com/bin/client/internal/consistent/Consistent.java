package com.bin.client.internal.consistent;

import com.bin.client.util.CRC32Util;
import com.bin.webmonitor.common.Pair;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class Consistent {
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private Lock readLock = rwLock.readLock();
    private Lock writeLock = rwLock.writeLock();

    private Map<Integer, Member> circle;
    private Map<String, Boolean> members;
    private List<Integer> sortedHashes;
    private int numberOfReplicas;
    private long count;

    private boolean useFnv;


    public Consistent() {
        numberOfReplicas = 20;
        circle = new HashMap<>();
        members = new HashMap<>();
    }


    private String eltKey(String elt, int idx) {
        return idx + elt;
    }

    public void add(Member m) {
        writeLock.lock();
        try {
            for (int i = 0; i < numberOfReplicas; i++) {
                circle.put(hashKey(eltKey(m.scheme(), i)), m);
            }
            members.put(m.scheme(), true);
            updateSortedHashes();
            count++;
        } finally {
            writeLock.unlock();
        }
    }


    public void remove(Member m) {
        writeLock.lock();
        try {
            remove(m.scheme());
        } finally {
            writeLock.unlock();
        }
    }

    private void remove(String elt) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashKey(eltKey(elt, i)));
        }
        members.remove(elt);
        updateSortedHashes();
        count--;
    }


    public void set(List<Member> elts) {
        writeLock.lock();
        try {
            Set<String> membersKeys = members.keySet();
            Set<String> memberSet = elts.stream().map(Member::scheme).collect(Collectors.toSet());
            for (String etl : membersKeys) {
                if (!memberSet.contains(etl))
                    remove(etl);
            }


            for (Member member : elts) {
                boolean exists = members.get(member.scheme());
                if (exists) continue;
                add(member);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public Member get(String name) {
        readLock.lock();
        try {
            Map<Integer, Member> circle = this.circle;
            if (circle.size() == 0)
                return null;

            int key = hashKey(name);
            int i = search(key);
            return circle.get(sortedHashes.get(i));
        } finally {
            readLock.unlock();
        }
    }

    private int search(int key) {
        int i = Collections.binarySearch(sortedHashes, key);
        if (i > sortedHashes.size())
            i = 0;
        return i;
    }


    public Pair<Member, Member> getTwo(String name) {
        readLock.lock();

        try {

            if (circle.size() == 0)
                return null;

            Member a = null;
            Member b = null;
            int key = hashKey(name);
            int i = search(key);
            a = circle.get(sortedHashes.get(i));
            if (count == 1)
               return Pair.of(a,b);

            int start = i;
            for (i = start + 1;i != start;i++) {
                if (i >= sortedHashes.size())
                    i = 0;
                b = circle.get(sortedHashes.get(i));
                if (!b.scheme().equals(a.scheme()))
                    break;
            }
            return Pair.of(a,b);
        } finally {
            readLock.unlock();
        }

    }

    public List<Member> getN(String name,int n) {
        readLock.lock();
        try{
            if (circle.size() == 0)
                return null;

            if (count < (long)n){
                n = (int) count;
            }

            int key = hashKey(name);
            int i = search(key);
            int start = i;
            Member elem = circle.get(sortedHashes.get(i));
            List<Member> res = new ArrayList<>(n);
            res.add(elem);

            if (n == 1) return res;

            for (i = start + i;i != start;i++) {
                if (i > sortedHashes.size())
                     i = 0;
                elem = circle.get(sortedHashes.get(i));
                if (!sliceContainsMember(res,elem))
                    res.add(elem);

                if (res.size() == n)
                    break;
            }

            return res;
        }finally {
            readLock.unlock();
        }
    }


    private boolean sliceContainsMember(List<Member> set,Member member) {
        for (Member m : set) {
            if (m.scheme().equals(member.scheme()))
                return true;
        }
        return false;
    }

    public Set<String> members() {
        readLock.lock();
        try {
            return members.keySet();
        } finally {
            readLock.unlock();
        }
    }


    private int hashKey(String key) {
        if (useFnv) {
            return hashKeyFnv(key);
        }
        return hashKeyCRC32(key);
    }


    private int hashKeyFnv(String key) {
        return Objects.hashCode(key);
    }

    private void updateSortedHashes() {

        List<Integer> hashes;
        if ((sortedHashes.size() / numberOfReplicas * 4) > circle.size()) {
            hashes = new ArrayList<>();
        } else {
            hashes = new ArrayList<>(sortedHashes);
        }

        Set<Integer> integers = circle.keySet();
        hashes.addAll(integers);

        Collections.sort(hashes);
        this.sortedHashes = hashes;
    }

    private int hashKeyCRC32(String key) {
        byte[] keys = key.getBytes(StandardCharsets.UTF_8);
        if (key.length() < 64) {
            byte[] scratch = new byte[64];
            System.arraycopy(keys, 0, scratch, 0, keys.length);
        }
        return (int) CRC32Util.encode(keys);
    }


    public void setNumberOfReplicas(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public void setCount(long count) {
        this.count = count;
    }



    public void setUseFnv(boolean useFnv) {
        this.useFnv = useFnv;
    }
}
