package com.bin.webmonitor.naming;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceNodes {

    @Value("system.env:online")
    private String onlineEnv;
    @Value("system.env:gray")
    private String grayEnv;
    @Value("system.server:docker")
    private String docker;

    public boolean isOnline(String systemEnv) {
        if (onlineEnv.equals(systemEnv)) return true;
        return false;
    }


    public boolean isGray(String systemEnv) {
        if (grayEnv.equals(systemEnv)) return true;
        return false;
    }

    public boolean isDocker(String serverType) {
        if (docker.equals(serverType)) return true;
        return false;
    }


}
