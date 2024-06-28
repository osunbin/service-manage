package com.bin.client.internal.cpu;

import java.lang.management.ManagementFactory;

public class JvmHelper {



    public static int getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
       return Integer.parseInt(name.split("@")[0]);
    }
}
