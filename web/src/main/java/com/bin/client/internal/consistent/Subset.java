package com.bin.client.internal.consistent;

import com.sun.jna.platform.win32.WinDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Subset {


    public static List<Member> subset(String selectKey, List<Member> inss, int num) {
        if (inss.size() <= num)
            return inss;

        Consistent consistent = new Consistent();
        consistent.setNumberOfReplicas(160);
        consistent.setUseFnv(true);
        consistent.set(inss);
        return subset(consistent, selectKey, inss, num);
    }

    public static List<Member> subset(Consistent c, String selectKey, List<Member> inss, int num) {
        List<Member> backends = c.getN(selectKey, num);
        if (backends == null || backends.isEmpty())
            return inss;

        return backends;
    }


    public static void main(String[] args) {
        List<Member> expect = List.of(() -> "2", () -> "3");
        List<Member> result = subset("1", List.of(() -> "2", () -> "2", () -> "2", () -> "3"), 3);

        System.out.println(expect);
        System.out.println(result);



    }


    public static void testDistribution() {
        String content = ""; //  backends.json 转 list

        Map<Member, Long> res = new HashMap<>();

        List<Member> backends = new ArrayList<>();

        Consistent consistent = new Consistent();
        consistent.setNumberOfReplicas(160);
        consistent.setUseFnv(true);
        consistent.set(backends);

        for (int i = 0;i < 8000;i++) {
            String id = UUID.randomUUID().toString();
            List<Member> backs = subset(consistent, id, backends, 25);
            for (Member back : backs) {
                Long index = res.getOrDefault(back, 0L);
                res.put(back,index + 1);
            }
        }

        System.out.println(res);
    }



}
