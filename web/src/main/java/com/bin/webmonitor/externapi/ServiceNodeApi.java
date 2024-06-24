package com.bin.webmonitor.externapi;

import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.naming.model.Status;
import com.bin.webmonitor.environment.EnvManager;
import com.bin.webmonitor.service.ServiceNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/extern/api/node/")
public class ServiceNodeApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EnvManager envManager;

    @Autowired
    private ServiceNodeService serviceNodeService;

    @Autowired
    private NamingProxy namingProxy;

    @RequestMapping("/removeServiceNode")
    public ServiceResult<Boolean> removeServiceNode(String serviceName, String ip) {
        try {
            if (!envManager.isTest()) {
                return new ServiceResult<>(new Status(-1, "非法调用！请确认调用是否为测试环境接口！！"), false);
            }
            ServiceResult<Boolean> res = serviceNodeService.removeNodeService(serviceName, ip);
            logger.info("op=end_removeNodeService,serviceName={},ip={},res={}", serviceName, ip, res);
            return res;
        } catch (Throwable e) {
            logger.error("[removeNodeService]移除测试环境节点上的单个服务失败！节点IP:{}  服务名称:{}",  ip, serviceName,e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @RequestMapping("/removeAllServices")
    public ServiceResult<Boolean> removeAllServices(String ip) {
        try {
            if (!envManager.isTest()) {
                return new ServiceResult<>(new Status(-1, "非法调用！请确认调用是否为测试环境接口！！"), false);
            }
            ServiceResult<Boolean> res = serviceNodeService.removeAllService(ip);
            logger.info("op=end_removeNodeService,ip={},res={}", ip, res);
            return res;
        } catch (Throwable e) {
            logger.error("[removeNodeService]移除测试环境节点上的所有服务失败！节点IP:{} ", ip, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/getAllServices")
    public ServiceResult<List<ServiceNode>> getAllServices(String ip) {

        try {
            List<ServiceNode> result = (List<ServiceNode>)namingProxy.getServiceNodeByIp(ip).checkAndReturn();
            logger.info("op=end_getAllServiceName,ip={},res={}", ip, result);
            return new ServiceResult<>(ServiceResult.SUCCESS, result);
        } catch (Throwable e) {
            logger.error("[ERROR-getAllServiceName]ip={}", ip, e);
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
    }

}
