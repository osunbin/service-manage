package com.bin.webmonitor;


import com.bin.webmonitor.repository.DataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *   期望管理:
 *   rpc
 *   mq
 *   定時任務
 */
@EnableAsync
@EnableScheduling
@Import(DataSourceConfig.class)
@SpringBootApplication(scanBasePackages = "com.bin.webmonitor",
        exclude = DataSourceAutoConfiguration.class)
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }



}
