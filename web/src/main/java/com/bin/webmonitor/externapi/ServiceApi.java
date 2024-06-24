package com.bin.webmonitor.externapi;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.component.ServiceValidator;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.CoreService;
import com.bin.webmonitor.service.ServiceNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/extern/api/service/")
public class ServiceApi {

    private static final Logger logger = LoggerFactory.getLogger(ServiceApi.class);

    @Autowired
    private ServiceValidator serviceValidator;

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private CoreService coreService;

    @Autowired
    private LocalService localService;

    @Autowired
    private ServiceNodeService serviceNodeService;

    @RequestMapping("/checkServiceName")
    public ApiResult<Boolean> checkServiceName(String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String remoteIp) {
        logger.info("op=start_checkServiceName,serviceName={},remoteIp={}", serviceName, remoteIp);
        ApiResult<Boolean> res = serviceValidator.validateServiceName(serviceName);
        if (Objects.nonNull(res)) {
            return res.setResult(false);
        }
        ServiceInstance dbService = serviceDao.selectByName(serviceName);
        if (Objects.nonNull(dbService)) {
            return new ApiResult<Boolean>(-1, "已存在服务名", serviceName).setResult(false);
        }
        return new ApiResult<>(0, "", "success", true);
    }

    @RequestMapping("/registerService")
    public ApiResult<Boolean> registerService(String serviceName, String owners, Long orgId, String description, @RequestParam(value = "serviceUsage", defaultValue = "0", required = false) boolean isAllowServiceGranularity,
                                              @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String remoteIp) {
        logger.info("op=start_registerService,serviceName={},owners={},orgId={},description={},functionCallOnly={},remoteIp={}", serviceName, owners, orgId, description, isAllowServiceGranularity, remoteIp);
        try {
            int maxPort = coreService.maxPort();
            ServiceInstance service = new ServiceInstance().setServiceName(serviceName).setOwners(owners).setDescription(description).setTcpPort(maxPort).setTelnetPort(maxPort + 10000);
            ApiResult<Boolean> result = coreService.register(service);

            ServiceInstance res = localService.getByName(service.getServiceName());
            logger.info("op=start_registerService, serviceName={},owners={},orgId={},description={},functionCallOnly={},res={},remoteIp={}", serviceName, owners, orgId, description, isAllowServiceGranularity, res, remoteIp);
            return result;
        } catch (Throwable e) {
            logger.error("[error_registerService]user=serviceManagerCenter,service={},owners={},orgId={},description={},functionCallOnly={},remoteIp={}", serviceName, owners, orgId, description, isAllowServiceGranularity, remoteIp, e);
            return new ApiResult<Boolean>().setCode(Constants.SERVER_ERROR).setMessage("平台内部出现错误");
        }
    }

    @RequestMapping("/getServiceByName")
    public ApiResult<ServiceInstance> getServiceByName(String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String remoteIp) {
        logger.info("op=start_getService,serviceName={},remoteIp={}", serviceName, remoteIp);

        if (StringUtil.isEmpty(serviceName)) {
            return new ApiResult<>(-1, "服务名称为空", "error");
        }

        ServiceInstance service = serviceDao.selectByName(serviceName);
        if (Objects.isNull(service)) {
            return new ApiResult<>(-1, "服务不存在", "error");
        }
        logger.info("op=end_getServiceByName,service={},remoteIp={}", service, remoteIp);
        return new ApiResult<ServiceInstance>().setCode(0).setResult(service);
    }



}
