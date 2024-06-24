package com.bin.webmonitor.repository.cache;

import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class LocalService {

    @Autowired
    private ServiceDao serviceInstanceDao;

    private final LoadingCache<String, ServiceInstance> serviceNameCache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(Integer.MAX_VALUE)
        .build(new CacheLoader<String, ServiceInstance>() {
            @Override
            public ServiceInstance load(String key) throws Exception {
                return serviceInstanceDao.selectByName(key);
            }
        });

    private final LoadingCache<Integer, ServiceInstance> serviceIdCache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(Integer.MAX_VALUE)
        .build(new CacheLoader<Integer, ServiceInstance>() {
            @Override
            public ServiceInstance load(Integer key) throws Exception {
                return serviceInstanceDao.selectById(key);
            }
        });

    /**
     * 根据服务名称查询，例如：UMC、IMC
     */
    public ServiceInstance getByName(String serviceName) {
        try {
            return serviceNameCache.get(serviceName);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public ServiceInstance getById(int id) {
        try {
            return serviceIdCache.get(id);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input id " + id, e);
        }
    }

    public String getServiceNameById(int id) {
        try {
            return serviceIdCache.get(id).getServiceName();
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input id " + id, e);
        }
    }

    public Map<Integer, ServiceInstance> getByIds(Collection<Integer> ids) {
        try {
            return serviceIdCache.getAll(ids);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input ids " + ids, e);
        }
    }

    public Map<Integer, String> getServiceNamesByIds(Collection<Integer> ids) {
        try {
            Map<Integer, ServiceInstance> serviceMap = serviceIdCache.getAll(ids);
            Map<Integer, String> res = new HashMap<>(serviceMap.size());
            serviceMap.entrySet().forEach(entry -> res.put(entry.getKey(), entry.getValue().getServiceName()));
            return res;
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input ids " + ids, e);
        }
    }

}
