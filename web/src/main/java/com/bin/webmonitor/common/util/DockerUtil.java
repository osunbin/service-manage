package com.bin.webmonitor.common.util;

import java.util.Set;

public class DockerUtil {

    public static boolean isDockerIp(String ip, Set<String> dockerIps) {
        if (dockerIps.contains(ip)) return true;
        for (String docker : dockerIps) {
           if (ip.startsWith(docker))
               return true;
        }
        return false;
    }
}