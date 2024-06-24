/*
package com.bin.client.dynamic;

import com.bin.client.util.ReflectUtil;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class DubboThreadPool implements ApplicationListener<ApplicationStartedEvent> {

    private static Logger logger = LoggerFactory.getLogger(DubboThreadPool.class);



    private final Map<String, ThreadPoolExecutor> dubboExecutor = new HashMap<>();


    public boolean changeThreadPool(ThreadPoolParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        ThreadPoolExecutor executor = dubboExecutor.get(threadPoolAdapterParameter.getThreadPoolKey());
        if (executor == null) {
            logger.warn("[{}] Dubbo consuming thread pool not found.", threadPoolKey);
            return false;
        }
        int originalCoreSize = executor.getCorePoolSize();
        int originalMaximumPoolSize = executor.getMaximumPoolSize();
        executor.setCorePoolSize(threadPoolAdapterParameter.getCorePoolSize());
        executor.setMaximumPoolSize(threadPoolAdapterParameter.getMaximumPoolSize());
        logger.info("[{}] Dubbo consumption thread pool parameter change. coreSize: {}, maximumSize: {}",
                threadPoolKey,
                String.format("%s => %s", originalCoreSize, executor.getCorePoolSize()),
                String.format("%s => %s", originalMaximumPoolSize, executor.getMaximumPoolSize()));
        return true;
    }



    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        boolean isLegacyVersion = false;
        String poolKey = ExecutorService.class.getName();

        try {

            ExecutorRepository executorRepository = ApplicationModel.defaultModel().getDefaultModule().getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
            ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>> data =
                    (ConcurrentMap<String, ConcurrentMap<Integer, ExecutorService>>) ReflectUtil.getFieldValue(executorRepository, "data");
            ConcurrentMap<Integer, ExecutorService> executorServiceMap = data.get(poolKey);
            executorServiceMap.forEach((key, value) -> dubboExecutor.put(String.valueOf(key), (ThreadPoolExecutor) value));
        } catch (Exception ex) {
            logger.error("Failed to get Dubbo {}.X protocol thread pool", isLegacyVersion ? "2" : "3", ex);
        }
    }
}
*/
