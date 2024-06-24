package com.bin.client.dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DynamicThreadPool {

    private static Logger logger = LoggerFactory.getLogger(DynamicThreadPool.class);

    private final Map<String, ThreadPoolExecutor> dynamicExecutor = new HashMap<>();


    public boolean changeThreadPool(ThreadPoolExecutor executor ,ThreadPoolParameter parameter) {

        if (parameter.getCorePoolSize() != null && parameter.getMaximumPoolSize() != null) {
            //如果服务端更新之后的最大线程数小于当前客户端线程池的最大线程数
            if (parameter.getMaximumPoolSize() < executor.getMaximumPoolSize()) {
                //那就先更新核心线程数，防止最大线程数小于本地线程池的核心线程数
                executor.setCorePoolSize(parameter.getCorePoolSize());
                //然后再更新本地现成的最大线程数
                executor.setMaximumPoolSize(parameter.getMaximumPoolSize());
            } else {
                //走到这里就意味着服务端的最大线程数大于本地的最大线程数
                //那就可以先更新最大线程数，然后再更新核心线程数
                executor.setMaximumPoolSize(parameter.getMaximumPoolSize());
                executor.setCorePoolSize(parameter.getCorePoolSize());
            }
        } else {
            if (parameter.getMaximumPoolSize() != null) {
                executor.setMaximumPoolSize(parameter.getMaximumPoolSize());
            }
            if (parameter.getCorePoolSize() != null) {
                executor.setCorePoolSize(parameter.getCorePoolSize());
            }
        }
        if (parameter.getCapacity() != null
                && Objects.equals("ResizableCapacityLinkedBlockingQueue", parameter.getQueueName())) {
            if (executor.getQueue() instanceof ResizableLinkedBlockingQueue) {
                ResizableLinkedBlockingQueue queue = (ResizableLinkedBlockingQueue) executor.getQueue();
                queue.setCapacity(parameter.getCapacity());
            } else {
                logger.warn("The queue length cannot be modified. Queue type mismatch. Current queue type: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
        if (parameter.getKeepAliveTime() != null) {
            executor.setKeepAliveTime(parameter.getKeepAliveTime(), TimeUnit.SECONDS);
        }
        if (parameter.getRejectedName() != null) {
            RejectedExecutionHandler rejectedExecutionHandler =
                    RejectedPolicyTypeEnum.createPolicy(parameter.getRejectedName());
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }

        if (executor.allowsCoreThreadTimeOut() != parameter.isAllowCoreThreadTimeOut()) {
            executor.allowCoreThreadTimeOut(parameter.isAllowCoreThreadTimeOut());
        }

        return true;
    }
}
