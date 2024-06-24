package com.bin.webmonitor.repository.cache;

import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Repository
public class LocalCaller {
    
    @Autowired
    private CallerDao callerDao;
    
    private final LoadingCache<String, Caller> callerKeyCache = CacheBuilder.newBuilder().maximumSize(2000).expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, Caller>() {
        @Override
        public Caller load(String key)
            throws Exception {
            return callerDao.selectByKey(key);
        }
    });
    
    private final LoadingCache<Integer, Caller> callerIdCache = CacheBuilder.newBuilder().maximumSize(2000).expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<Integer, Caller>() {
        @Override
        public Caller load(Integer id)
            throws Exception {
            return callerDao.selectByPrimaryKey(id);
        }
    });
    
    /**
     * 根据调用者秘钥进行查询
     */
    public Caller getByCallerKey(String callerKey) {
        try {
            return callerKeyCache.get(callerKey);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public Caller getById(Integer id) {
        try {
            return callerIdCache.get(id);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input id " + id, e);
        }
    }
    
    public Map<Integer, Caller> getByIds(Collection<Integer> ids) {
        try {
            return callerIdCache.getAll(ids);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input ids " + ids, e);
        }
    }
}
