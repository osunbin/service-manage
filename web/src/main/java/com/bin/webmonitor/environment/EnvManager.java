package com.bin.webmonitor.environment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvManager {

    @Autowired
    private Environment environment;

    public boolean isTest() {
        return getCurrentEnvironment() == EnvironmentEnum.TEST;
    }

    public boolean isDev() {
        return getCurrentEnvironment() == EnvironmentEnum.DEV;
    }

    public boolean isProd() {
        return getCurrentEnvironment() == EnvironmentEnum.PROD;
    }

    public EnvironmentEnum getCurrentEnvironment() {
        if (environment.getActiveProfiles() == null || environment.getActiveProfiles().length == 0) {
            return EnvironmentEnum.UNKNOWN;
        }

        String profile = environment.getActiveProfiles()[0];
        return EnvironmentEnum.codeOf(profile);
    }
}
