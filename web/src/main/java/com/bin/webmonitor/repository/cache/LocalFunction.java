package com.bin.webmonitor.repository.cache;

import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class LocalFunction {

    @Autowired
    private ServiceFunctionDao serviceFunctionDao;

    LoadingCache<Integer, ServiceFunction> functionIdMap = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build(new CacheLoader<Integer, ServiceFunction>() {
        @Override
        public ServiceFunction load(Integer fid)
                throws Exception {
            return serviceFunctionDao.getServiceFunctionsByid(fid);
        }
    });

    private static final String SID_FUNCTION_NAME_SPLITTER = ":";

    LoadingCache<String, ServiceFunction> sidFunctionNameMap = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build(new CacheLoader<String, ServiceFunction>() {
        @Override
        public ServiceFunction load(String sidFunctionName)
                throws Exception {
            String[] sidFunctionNameArray = sidFunctionName.split(SID_FUNCTION_NAME_SPLITTER);
            int sid = Integer.parseInt(sidFunctionNameArray[0]);
            String method = sidFunctionNameArray[1];

            return serviceFunctionDao.getServiceFunctionsBySidAndFname(sid, method);
        }
    });

    public String function(Integer functionId) {
        try {
            ServiceFunction serviceFunction = functionIdMap.get(functionId);
            if (serviceFunction != null) {
                return StringUtil.isBlank(serviceFunction.getGenericMethodSignature()) ? serviceFunction.getGeneralMethodSignature() : serviceFunction.getGenericMethodSignature();
            } else {
                return null;
            }
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input id " + functionId, e);
        }
    }

    public Map<Integer, String> getNamesByIds(Collection<Integer> functionIds) {
        try {
            Map<Integer, ServiceFunction> serviceFunctionMap = functionIdMap.getAll(functionIds);
            Map<Integer, String> res = new HashMap<>(serviceFunctionMap.size());
            serviceFunctionMap.entrySet().forEach(entry -> res.put(entry.getKey(), StringUtil.isBlank(entry.getValue().getGenericMethodSignature()) ? entry.getValue().getGeneralMethodSignature() : entry.getValue().getGenericMethodSignature()));
            return res;
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input ids " + functionIds, e);
        }
    }

    public ServiceFunction serviceFunction(Integer functionId) {
        try {
            return functionIdMap.get(functionId);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input id " + functionId, e);
        }

    }

    public Map<Integer, ServiceFunction> serviceFunctions(Collection<Integer> functionIds) {
        try {
            return functionIdMap.getAll(functionIds);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input ids " + functionIds, e);
        }

    }

    /**
     * 根据(sid,functionName)获取function信息
     *
     * @param sid
     * @param functionName
     * @return
     */
    public ServiceFunction serviceFunctionByName(int sid, String functionName) {
        try {
            String sidFunctionNameKey = sid + SID_FUNCTION_NAME_SPLITTER + functionName;
            return sidFunctionNameMap.get(sidFunctionNameKey);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("input sid=" + sid + ",functionName=" + functionName, e);
        }
    }

    public Integer functionIdByName(int sid, String functionName) {
        ServiceFunction serviceFunction = this.serviceFunctionByName(sid, functionName);
        if (Objects.isNull(serviceFunction)) {
            return null;
        }

        return serviceFunction.getId();
    }

}
