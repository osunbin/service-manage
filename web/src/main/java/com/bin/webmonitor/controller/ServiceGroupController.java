package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.environment.EnvManager;
import com.bin.webmonitor.model.ServiceGroup;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.CoreService;
import com.bin.webmonitor.service.OperateRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/service/group")
public class ServiceGroupController {


    private static Logger logger = LoggerFactory.getLogger(ServiceGroupController.class);

    @Autowired
    private CoreService coreService;

    @Autowired
    private NamingProxy namingProxy;

    @Autowired
    private LocalService localService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private EnvManager envManager;

    @RequestMapping("/add")
    public ApiResult<Boolean> addGroup( ServiceInstance service, String groupName, @RequestParam String ips) {
        logger.info("add group {} ips {} to service {}",  groupName, ips, service.getServiceName());
        if (StringUtil.isBlank(groupName) || groupName.length() > 20) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("组名长度范围【1-20】");
        }
        return coreService.addGroup(service, groupName, ips);
    }

    @RequestMapping("/update")
    public ApiResult<Boolean> updateGroup( ServiceInstance service, @RequestParam int gid, @RequestParam String groupName, @RequestParam String ips) {
        logger.info(" update group {} ips {} to service {}",  groupName, ips, service.getServiceName());
        operateRecordService.addOperateRecord( null, service.getId(), OperateRecordType.SERVICE_NODEGROUP_UPDATE,
                "更新分组" + groupName + "的节点列表：" + ips);
        return coreService.updateGroup( service, gid, groupName, ips);
    }

    @RequestMapping("/list")
    public ApiResult<Map<String,Object>> list(String serviceName, int sid) {
        Map<String,Object> modelMap = new HashMap<>();
        List<ServiceGroup> serviceGroups = coreService.groupList(serviceName, sid);
        modelMap.put("service", localService.getById(sid));
        modelMap.put("serviceGroups", serviceGroups);
        modelMap.put("systemEnv", envManager.getCurrentEnvironment().getCode());
        try {
            modelMap.put("ips", namingProxy.getNodes(serviceName).checkAndReturn().stream().map(ServiceNode::getIp).collect(Collectors.toList()));
        } catch (Throwable e) {
            modelMap.put("ips", Collections.emptyMap());
            logger.error("from control get nodes error serviceName {}", serviceName, e);
        }
        return new ApiResult<Map<String,Object>>().setResult(modelMap);
    }

    @RequestMapping("/updateStatus")
    public ApiResult<Boolean> updateStatus(@RequestParam int gid, @RequestParam int status) {
        logger.info("op=start_updateStatus,gid={},status={}", gid, status);
        return coreService.updateGroupStatus(gid, status);
    }

    @RequestMapping("/delete")
    public ApiResult<Boolean> delete(ServiceInstance service, @RequestParam int gid, String groupName) {
        logger.info("delete service {} function group {} gid {}", service.getServiceName(), groupName, gid);
        operateRecordService.addOperateRecord( null, service.getId(), OperateRecordType.SERVICE_NODEGROUP_DELETE, "删除分组:" + groupName);
        return coreService.deleteGroup( service, gid);
    }

}
