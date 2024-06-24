package com.bin.webmonitor.externapi;

import com.bin.webmonitor.component.ExternService;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/extern/api/")
public class ExternApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ExternService externService;


    @RequestMapping("/getServiceInfo")
    public ServiceResult<ServiceInstance> getServiceInfo(String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For") String callerIp) {
        try {
            ServiceResult<ServiceInstance> res = externService.getServiceInfo(serviceName);

            logger.info("op=end_getServiceInfo,serviceName={},callerIp={},res={}", serviceName, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR-getServiceInfo]serviceName={},callerIp={}", serviceName, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getGroupInfo")
    public ServiceResult<List<Group>> getGroupInfo(String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For") String callerIp) {
        try {
            ServiceResult<List<Group>> res = externService.getGroupInfo(serviceName);
            logger.info("op=end_getGroupInfo,serviceName={},callerIp={},res={}", serviceName, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR-getGroupInfo]serviceName={},callerIp={}", serviceName, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }
}
