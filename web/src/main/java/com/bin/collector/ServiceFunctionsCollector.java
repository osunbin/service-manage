package com.bin.collector;

import com.bin.collector.request.ServiceFunctions;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 *  服务方法请求对象：服务注册使
 */
@Component
public class ServiceFunctionsCollector {

    private static Logger logger = LoggerFactory.getLogger(ServiceFunctionsCollector.class);

    @Autowired
    private ServiceFunctionDao serviceFunctionDao;

    @Autowired
    private LocalService localService;
    public void collect(ServiceFunctions request, String ip) {
        logger.info("from {} service function request {}", ip, request);
        ServiceInstance service;
        try {
            service = localService.getByName(request.getServiceName());
        } catch (Exception e) {
            logger.error("retryGet service {} error ", request.getServiceName(), e);
            return;
        }

        if (service == null) {
            logger.warn("service {} not in server manager ", request.getServiceName());
            return;
        }
        try {
            request.getFunctionNames().forEach((String sn) -> {
                ServiceFunction serviceFunction = new ServiceFunction(service.getId(), sn, ip);
                try {
                    serviceFunctionDao.add(serviceFunction);
                } catch (DuplicateKeyException e1) {
                } catch (Exception e) {
                    logger.error("insert service function error {}", serviceFunction, e);
                }
            });
        } catch (Exception e2) {
            logger.error("insert error", e2);
        }

    }

}
