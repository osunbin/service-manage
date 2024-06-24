package com.bin.webmonitor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.Executor;

@Configuration
public class BaseConfig implements AsyncConfigurer, EnvironmentAware {

    private static final Logger log = LoggerFactory.getLogger(BaseConfig.class);

    Environment env;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(32);
        taskExecutor.setMaxPoolSize(64);
        taskExecutor.setQueueCapacity(64);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        log.warn("AopConfig.getAsyncUncaughtExceptionHandler");
        return new AsyncExceptionHandler();
    }





    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
            log.warn("Exception message - " + throwable.getMessage());
            log.warn("Method name - " + method.getName());
            for (Object param : obj) {
                log.warn("Parameter value - " + param);
            }
        }
    }
}
