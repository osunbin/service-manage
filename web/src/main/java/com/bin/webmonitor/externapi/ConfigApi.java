package com.bin.webmonitor.externapi;

import com.bin.webmonitor.component.ConfigService;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta;
import com.bin.webmonitor.model.ServerNode;
import com.bin.webmonitor.model.ServiceCountMeta;
import com.bin.webmonitor.model.ServiceFunctions;
import com.bin.webmonitor.model.ServiceRejectMeta;
import com.bin.webmonitor.model.ServerConfig;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigApi {

    private static Logger logger = LoggerFactory.getLogger(ConfigApi.class);

    @Autowired
    private ConfigService configService;

    @RequestMapping("/getConfig")
    public ServiceResult<ServerConfig> getConfig(String callerKey, String serviceName, long clientConfigTime, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {

            ServiceResult<ServerConfig> res = configService.getConfig(callerKey, serviceName, callerIp, clientConfigTime);

            logger.info("op=end_getConfig,callerKey={},serviceName={},clientConfigTime={},callerIp={},res={}", callerKey, serviceName, clientConfigTime, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getConfig]callerKey={},serviceName={},clientConfigTime={},callerIp={}", callerKey, serviceName, clientConfigTime, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getCloudNodes")
    public ServiceResult<List<ServerNode>> getCloudNodes(String callerKey, String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<List<ServerNode>> res = configService.getAllAvailableCloudNodesInGroup(serviceName);

            logger.info("op=end_getCloudNodes,callerKey={},serviceName={},callerIp={},res={}", callerKey, serviceName, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getCloudNodes]callerKey={},serviceName={},callerIp={}", callerKey, serviceName, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getServiceCountMeta")
    public ServiceResult<ServiceCountMeta> getServiceCountMeta(String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<ServiceCountMeta> res = configService.getServiceCountMeta(serviceName);

            logger.info("op=end_getServiceCountMeta,serviceName={},callerIp={},res={}", serviceName, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getServiceCountMeta]serviceName={},callerIp={}", serviceName, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getCallerCountMeta")
    public ServiceResult<?> getCallerCountMeta(String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<?> res = configService.getCallerCountMeta(serviceName);

            logger.info("op=end_getCallerCountMeta,serviceName={},callerIp={},res={}", serviceName, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getCallerCountMeta]serviceName={},callerIp={}", serviceName, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getCallerDegradeMeta")
    public ServiceResult<?> getCallerDegradeMeta(String callerKey, String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<?> res = configService.getCallerDegradeMeta(callerKey, serviceName);

            logger.info("op=end_getCallerDegradeMeta,callerKey={},serviceName={},callerIp={},res={}", callerKey, serviceName, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getCallerDegradeMeta]callerKey={},serviceName={},callerIp={}", callerKey, serviceName, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getCallerCircuitBreakConfigMeta")
    public ServiceResult<CallerCircuitBreakConfigMeta> getCallerCircuitBreakConfigMeta(@RequestParam("callerKey") String callerKey, @RequestParam(value = "serviceName", required = false) String serviceName, @RequestParam(value = "method", required = false) String method,
                                                                                       @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<CallerCircuitBreakConfigMeta> res = configService.getCallerCircuitBreakConfigMeta(callerKey, serviceName, method);

            logger.info("op=end_getCallerCircuitBreakConfigMeta,callerKey={},serviceName={},method={},callerIp={},res={}", callerKey, serviceName, method, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getCallerCircuitBreakConfigMeta]callerKey={},serviceName={},method={},callerIp={}", callerKey, serviceName, method, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getServiceRejectMeta")
    
    public ServiceResult<ServiceRejectMeta> getServiceRejectMeta(String serviceName, String callerKey, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<ServiceRejectMeta> res = configService.getServiceRejectMeta(serviceName, callerKey);

            logger.info("op=end_getServiceRejectMeta,serviceName={},callerKey={},callerIp={},res={}", serviceName, callerKey, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getServiceRejectMeta]serviceName={},callerKey={},callerIp={}", serviceName, callerKey, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/persistServiceFunctions")
    public ServiceResult<Boolean> persistServiceFunctions(@RequestBody ServiceFunctions request, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<Boolean> res = configService.persistServiceFunctions(request, callerIp);

            logger.info("op=end_persistServiceFunctions,request={},callerIp={},res={}", request, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_persistServiceFunctions]request={},callerIp={}", request, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/persistServiceFuncSignature")
    public ServiceResult<Boolean> persistServiceFuncSignature(@RequestBody ServiceFunctions request, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<Boolean> res = configService.persistServiceFunctionSignature(request, callerIp);

            logger.info("op=end_persistServiceFuncSignature,request={},callerIp={},res={}", request, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_persistServiceFuncSignature]request={},callerIp={}", request, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/uploadClientUsageConfigs")
    public ServiceResult<Boolean> uploadClientConfig(String callerKey, String usageConfigs, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            // 将其中unicode字符转换为正常
            String formatConfig = URLDecoder.decode(usageConfigs, "UTF-8");
            String callKey = URLDecoder.decode(callerKey, "UTF-8");

            ServiceResult<Boolean> res = configService.saveClientConfig(callKey, callerIp, formatConfig);
            logger.info("op=end_uploadClientConfig , callerKey={},callerIp={},usageConfigs={}", callKey, callerIp, formatConfig);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_uploadClientConfig]callerKey={},callerIp={},usageConfigs={}", callerKey, callerIp, usageConfigs, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/uploadServerConfigs")
    public ServiceResult<Boolean> uploadServerConfig(String serviceName, String config, String log4j2, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<Boolean> res = configService.saveServiceConfig(serviceName, config, log4j2, callerIp);
            logger.info("op=end_uploadServerConfig,serviceName={},callerIp={},config={},log4j2={}", serviceName, callerIp, config, log4j2);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_uploadServerConfig]serviceName={},callerIp={},config={},log4j2={}", serviceName, callerIp, config, log4j2, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/getServiceByServiceName")
    
    public ServiceResult<ServiceInstance> getServiceByServiceName(String serviceName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {
            ServiceResult<ServiceInstance> res = configService.getServiceByServiceName(serviceName);

            logger.info("op=end_getServiceByServiceName,serviceName={},callerIp={},res={}", serviceName, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[ERROR_getServiceByServiceName]serviceName={},callerIp={}", serviceName, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/uploadServiceExt")
    public ServiceResult<String> uploadServiceExt(String serviceName, String ext, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String callerIp) {
        try {

            ServiceResult<String> res = configService.saveServiceExt(serviceName, ext, callerIp);
            logger.info("op=end_uploadServiceExt,serviceName={},ext={},ip={},res={}", serviceName, ext, callerIp, res);
            return res;
        } catch (Throwable e) {
            logger.error("[error_uploadServiceExt]serviceName={},ext={},ip={}", serviceName, ext, callerIp, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }

    }

}
