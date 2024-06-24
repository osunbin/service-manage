package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.DiffUtil;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.component.ServiceValidator;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.environment.EnvManager;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.CoreService;
import com.bin.webmonitor.service.OperateRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/service")
public class ServiceController {

    private static Logger logger = LoggerFactory.getLogger(ServiceController.class);
    @Autowired
    private CoreService coreService;
    @Autowired
    private NamingProxy namingProxy;
    @Autowired
    private LocalService localService;
    @Autowired
    private EnvManager envManager;
    @Autowired
    private OperateRecordService operateRecordService;
    @Autowired
    private ServiceValidator serviceValidator;


    @RequestMapping("/register")
    public ApiResult<Boolean> register( ServiceInstance service) {

        try {
            logger.info(" create service {}",  service);
            ApiResult<Boolean> result = serviceValidator.validateService(service);
            if (result != null) {
                return result;
            }
            result = coreService.register(service);
            ServiceInstance afterService = localService.getByName(service.getServiceName());
            operateRecordService.addOperateRecord( null, afterService.getId(), OperateRecordType.SERVICE_REGISTER, "服务注册：" + afterService);
            logger.info(" create service {} result {}",  service, result);
            return result;
        } catch (Throwable e) {

            logger.error("uscreate service {} error",  service, e);
            return new ApiResult<Boolean>().setCode(Constants.SERVER_ERROR).setMessage("平台内部出现错误");
        }
    }

    @RequestMapping("/edit")
    public ApiResult<Boolean> edit(ServiceInstance service) {

        try {
            logger.info(" edit service {}",  service);
            ApiResult<Boolean> result = serviceValidator.validateService(service);
            if (result != null) {
                return result;
            }

            ServiceInstance beforeService = coreService.getById(service.getId());
            result = coreService.edit( service);
            ServiceInstance afterService = coreService.getById(service.getId());
            operateRecordService.addOperateRecord( null, service.getId(), OperateRecordType.SERVICE_UPDATE,
                    "更新服务:" + DiffUtil.diffObjectField(beforeService, afterService, Stream.of("updateTime").collect(Collectors.toSet())));
            logger.info(" edit service {} result {}",  service, result);
            return result;
        } catch (Throwable e) {

            logger.error(" edit service {} error",  service, e);
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("平台内部出现错误");
        }
    }



    @RequestMapping("/goDetail")
    public ApiResult<Map<String,Object>> goDetail(int id) {
        Map<String,Object> modelMap = new HashMap<>();
        ServiceInstance service = localService.getById(id);

        if (service == null) {
            modelMap.put("functions", Collections.emptyMap());
            modelMap.put("ips", Collections.emptyMap());
            modelMap.put("groups", Collections.emptyMap());
            modelMap.put("service", new ServiceInstance());
            return new ApiResult<Map<String,Object>>().setResult(modelMap).setCode(Constants.SUCCESS);
        }

        List<ServiceNode> nodes;
        try {
            nodes = namingProxy.getNodes(service.getServiceName()).checkAndReturn();
        } catch (Throwable e) {
            logger.error("control error", e);
            nodes = Collections.EMPTY_LIST;
        }
        //get functions
        List<ServiceFunction> functions = coreService.getFunctions(id);
        modelMap.put("functions", functions);
        modelMap.put("onlineIPs", nodes == null ? Collections.emptyMap() : nodes.stream().filter(ServiceNode::isRunning).map(ServiceNode::getIp).collect(Collectors.toList()));
        modelMap.put("offlineIPs", nodes == null ? Collections.emptyMap() : nodes.stream().filter(node -> !node.isRunning()).map(ServiceNode::getIp).collect(Collectors.toList()));
        modelMap.put("ips", nodes == null ? Collections.emptyMap() : nodes.stream().map(ServiceNode::getIp).collect(Collectors.toList()));
        //get groups
        List<Group> groups = coreService.getGroups(id);
        modelMap.put("groups", groups);
        modelMap.put("service", service);
        return new ApiResult<Map<String,Object>>().setResult(modelMap).setCode(Constants.SUCCESS);
    }

    @RequestMapping("/serviceList")
    public DataGrid<ServiceInstance> serviceList( @PageableDefault Pageable pageable, ServiceInstance service) {

        List<ServiceInstance> services = coreService.serviceList(pageable, service);
        int total = coreService.countServiceList(service);
        Map<String, Object> searchMap = new HashMap<>(6);
        if (!StringUtil.isEmpty(service.getServiceName())) {
            searchMap.put("serviceName", service.getServiceName());
        }
        if (!StringUtil.isEmpty(service.getOwners())) {
            searchMap.put("owners", service.getOwners());
        }

        if (service.getTcpPort() > 0) {
            searchMap.put("tcpPort", service.getTcpPort());
        }

        DataGrid<ServiceInstance> dataGrid = new DataGrid<>(pageable.getPageNumber(), pageable.getPageSize(), total, services, searchMap);
        return dataGrid;
    }



    @RequestMapping("/deleteService")
    public ApiResult<Boolean> deleteService( ServiceInstance service) {

        logger.info(" delete service {}", service.getServiceName());


        ServiceInstance beforeService = localService.getByName(service.getServiceName());
        ApiResult<Boolean> result = coreService.deleteService(service);
        if(result.getResult()){
            operateRecordService.addOperateRecord( null, service.getId(), OperateRecordType.SERVICE_DELETE, "删除服务方：" + beforeService);
        }
        logger.info("user {} delete service {} result {}",  service.getServiceName(), result);
        return result;
    }


}
