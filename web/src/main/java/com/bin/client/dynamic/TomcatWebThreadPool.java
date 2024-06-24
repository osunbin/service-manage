package com.bin.client.dynamic;

import com.bin.client.spring.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class TomcatWebThreadPool implements ApplicationRunner {

    private static Logger logger = LoggerFactory.getLogger(TomcatWebThreadPool.class);


    private volatile Executor executor;


    public WebServer getWebServer() {
        ApplicationContext applicationContext = ApplicationContextHolder.getInstance();
        WebServer webServer = ((WebServerApplicationContext) applicationContext).getWebServer();
        return webServer;
    }

    public Executor getWebThreadPool() {
        if (executor == null) {
            synchronized (TomcatWebThreadPool.class) {
                if (executor == null) {
                    //调用子类的方法得到web容器线程池
                    executor = getWebThreadPoolByServer(getWebServer());
                }
            }
        }
        return executor;
    }

    protected Executor getWebThreadPoolByServer(WebServer webServer) {

        Executor tomcatExecutor = null;
        try {
            tomcatExecutor = ((TomcatWebServer) webServer).getTomcat().getConnector().getProtocolHandler().getExecutor();
        } catch (Exception ex) {

            logger.error("Failed to get Tomcat thread pool.", ex);
        }
        return tomcatExecutor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {//在这里得到了web容器的线程池
            getWebThreadPool();
        } catch (Exception ignored) {
        }
    }


    public void changeThreadPool(ThreadPoolParameter threadPoolParameterInfo) {
        try {
            org.apache.tomcat.util.threads.ThreadPoolExecutor tomcatThreadPoolExecutor = (org.apache.tomcat.util.threads.ThreadPoolExecutor) executor;
            int originalCoreSize = tomcatThreadPoolExecutor.getCorePoolSize();
            int originalMaximumPoolSize = tomcatThreadPoolExecutor.getMaximumPoolSize();
            long originalKeepAliveTime = tomcatThreadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS);
            tomcatThreadPoolExecutor.setCorePoolSize(threadPoolParameterInfo.getCorePoolSize());
            tomcatThreadPoolExecutor.setMaximumPoolSize(threadPoolParameterInfo.getMaximumPoolSize());
            tomcatThreadPoolExecutor.setKeepAliveTime(threadPoolParameterInfo.getKeepAliveTime(), TimeUnit.SECONDS);
            logger.info("[Tomcat] Changed web thread pool. corePoolSize: {}, maximumPoolSize: {}, keepAliveTime: {}",
                    String.format("%s => %s", originalCoreSize, threadPoolParameterInfo.getCorePoolSize()),
                    String.format("%s => %s", originalMaximumPoolSize, threadPoolParameterInfo.getMaximumPoolSize()),
                    String.format("%s => %s", originalKeepAliveTime, threadPoolParameterInfo.getKeepAliveTime()));
        } catch (Exception ex) {
            logger.error("Failed to modify the Tomcat thread pool parameter.", ex);
        }
    }
}
