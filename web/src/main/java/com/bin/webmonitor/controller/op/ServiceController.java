package com.bin.webmonitor.controller.op;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.CallerUsageExtDao;
import com.bin.webmonitor.repository.dao.CircuitBreakConfigDao;
import com.bin.webmonitor.repository.dao.CircuitBreakEventDao;
import com.bin.webmonitor.repository.dao.CircuitBreakMonitorDao;
import com.bin.webmonitor.repository.dao.GroupDao;
import com.bin.webmonitor.repository.dao.ServiceConfigDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionGroupDao;
import com.bin.webmonitor.repository.dao.ServiceNodeGroupDao;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.CallerUsageExt;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.CircuitBreakConfig;
import com.bin.webmonitor.repository.domain.CircuitBreakEvent;
import com.bin.webmonitor.repository.domain.CircuitBreakMonitor;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceFunctionGroup;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.repository.domain.ServiceNodeGroup;
import com.bin.webmonitor.service.CallerUsageService;
import com.bin.webmonitor.service.CoreService;
import com.bin.webmonitor.service.OperateRecordService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bin.webmonitor.controller.BaseController;

@RestController("opServiceController")
@RequestMapping("/op/service")
public class ServiceController extends BaseController {

    @Autowired
    private ServiceDao serviceDaoy;
    @Autowired
    private CallerUsageService callerUsageService;
    @Autowired
    private NamingProxy namingProxy;

    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;
    @Autowired
    private ServiceFunctionDao serviceFunctionDao;
    @Autowired
    private CircuitBreakConfigDao circuitBreakConfigDao;
    @Autowired
    private CircuitBreakEventDao circuitBreakEventDao;
    @Autowired
    private CircuitBreakMonitorDao circuitBreakMonitorDao;
    @Autowired
    private CallerUsageExtDao callerUsageExtDao;
    @Autowired
    private GroupDao groupDaoy;
    @Autowired
    private ServiceNodeGroupDao serviceNodeGroupDao;
    @Autowired
    private ServiceConfigDao serviceConfigDao;
    @Autowired
    private ServiceFunctionGroupDao serviceFunctionGroupDaoy;

    @Autowired
    private OperateRecordService operateRecordService;
    @Autowired
    private CoreService coreService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "getServiceById", method = RequestMethod.GET)
    public ApiResult<String> getServiceById( @RequestParam("id") int id) {

        ServiceInstance service = serviceDaoy.selectById(id);

        logger.info("op=end_getServiceById,id={},service={}", id, service);
        return okJson(service);
    }


    @RequestMapping(value = "getServiceByName", method = RequestMethod.GET)
    public ApiResult<String> getServiceByName(@RequestParam("serviceName") String serviceName) {

        ServiceInstance service = serviceDaoy.selectByName(serviceName);

        logger.info("op=end_getServiceByName,serviceName={},service={}", serviceName, service);
        return okJson(service);
    }

    @RequestMapping(value = "changePort", method = RequestMethod.GET)
    public ApiResult<String> changePort( @RequestParam("serviceName") String serviceName, @RequestParam("tcpPort") int tcpPort, @RequestParam("date") String date) {
        logger.info("op=start_changePort,serviceName={},tcpPort={}",  serviceName, tcpPort);
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");
        ServiceInstance beforeService = serviceDaoy.selectByName(serviceName);
        if (Objects.isNull(beforeService)) {
            return errorJson("服务不存在！");
        }
        ServiceInstance service = serviceDaoy.selectByName(serviceName).setTcpPort(tcpPort);
        try {
            serviceDaoy.updateService(service);
        } catch (Exception e) {
            logger.error("update service failed!service={}", beforeService, e);
            return errorJson("更新服务失败！");
        }
        ServiceInstance afterService = serviceDaoy.selectByName(serviceName);
        coreService.sendConfigChange(service, null);

        Map<String, Object> res = new HashMap<>();
        res.put("before", beforeService);
        res.put("after", afterService);
        logger.info("op=end_changePort,serviceName={},tcpPort={},res={}",  serviceName, tcpPort, res);
        return okJson(res);
    }
    /**
     * 获取节点运行端口与分配端口不一致的服务方信息
     *
     * @return
     */
    @RequestMapping(value = "getWrongPortServices", method = RequestMethod.GET)
    public ApiResult<Object> getWrongPortServices() {
        Map<String, Map<String, Object>> res = new HashMap<>();

        List<ServiceInstance> services = serviceDaoy.selectAll();
        for (ServiceInstance service : services) {
            List<ServiceNode> serviceNodes = namingProxy.getNodes(service.getServiceName()).checkAndReturn();
            List<ServiceNode> onlineServiceNodes = serviceNodes.stream()
                    .filter(serviceNode -> serviceNode.isRunning() && serviceNode.isInService())
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(onlineServiceNodes)) {
                continue;
            }

            // 逐个检查端口号与分配的端口号是否一致
            for (ServiceNode onlineServiceNode : onlineServiceNodes) {
                if (onlineServiceNode.getPort() != service.getTcpPort()) {
                    Map<String, Object> serviceRes = new LinkedHashMap<>();
                    serviceRes.put("service", service);
                    serviceRes.put("onlineServiceNodes", onlineServiceNodes);
                    res.put(service.getServiceName(), serviceRes);
                    break;
                }
            }

        }

        logger.info("op=end_getWrongPortServices,res={}", res);
        return okJson(res);
    }

    /**
     * 不强制删除（如果还有调用关系，就不删除）
     *
     * @param serviceName
     * @param date 验证参数
     * @return
     */
    @RequestMapping(value = "deleteServiceByName", method = RequestMethod.GET)
    public ApiResult<Object> deleteServiceByName( @RequestParam("serviceName") String serviceName, @RequestParam("date") String date) {
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");
        ServiceInstance service = serviceDaoy.selectByName(serviceName);
        ApiResult<Object> res = deleteServiceByName(serviceName, false);
        if (res.getCode() == 0) {
            operateRecordService.addOperateRecord(null, service.getId(), OperateRecordType.SERVICE_DELETE, "不强制删除服务方" + service);
        }
        logger.info("op=end_deleteServiceByName,serviceName={},res={}",  serviceName, res);
        return res;
    }

    /**
     * 强制删除（会把相关的调用关系也删除）
     *
     * @param serviceName
     * @return
     */
    @RequestMapping(value = "forceDeleteServiceByName", method = RequestMethod.GET)
    public ApiResult<Object> forceDeleteServiceByName( @RequestParam("serviceName") String serviceName, @RequestParam("date") String date) {
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");
        ServiceInstance service = serviceDaoy.selectByName(serviceName);
        ApiResult<Object> res = deleteServiceByName(serviceName, true);
        if (res.getCode() == 0) {
            operateRecordService.addOperateRecord( null, service.getId(), OperateRecordType.SERVICE_DELETE, "强制删除服务方" + serviceName);
        }
        logger.info("op=end_forceDeleteServiceByName,serviceName={},res={}", serviceName, res);
        return res;
    }

    private ApiResult<Object> deleteServiceByName(String serviceName, boolean isForce) {
        logger.info("op=start_deleteServiceByName,serviceName={},isForce={}", serviceName, isForce);
        ServiceInstance service = serviceDaoy.selectByName(serviceName);
        if (Objects.isNull(service)) {
            return errorJson(String.format("服务[%s]不存在", serviceName));
        }
        if (!Objects.equals(serviceName, service.getServiceName())) {
            return errorJson(String.format("服务方[%s]与DB中的名称[%s]不匹配，请检查！！！", serviceName, service.getServiceName()));
        }

        List<CallerUseage> callerUseages = callerUsageService.getCallerUsageList(null, service.getId());
        if (!isForce) {
            // 如果不强制，要进行检查
            if (!CollectionUtils.isEmpty(callerUseages)) {
                return errorJson("有调用关系，不能删除", callerUseages);
            }
        } else {
            // 删除调用关系
            deleteServiceComplete(service);
        }

        boolean deleted = serviceDaoy.deleteService(service.getId());
        ServiceInstance afterService = serviceDaoy.selectByName(serviceName);
        List<CallerUseage> afterCallerUseages = callerUsageService.getCallerUsageList(null, service.getId());

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("beforeService", service);
        res.put("afterService", afterService);
        res.put("beforeCallerUseages", callerUseages);
        res.put("afterCallerUseages", afterCallerUseages);
        res.put("deleted", deleted);

        logger.info("op=end_deleteServiceByName,serviceName={},isForce={},res={}", serviceName, isForce, res);
        return okJson(res);
    }

    private void deleteServiceComplete(ServiceInstance service) {
        logger.info("op=start_deleteServiceComplete,service={}", service);

        String serviceName = service.getServiceName();
        //获取调用关系
        List<CallerUseage> callerUseages = callerUsageService.getCallerUsageList(null, service.getId());
        if (CollectionUtils.isEmpty(callerUseages)) {
            callerUseages = new ArrayList<>();
        }

        //删除调用关系
        for (CallerUseage item : callerUseages) {
            int deleteCount = callerUsageService.deleteCallerUsage(item.getId(), item.getCid(), item.getSid());
            logger.info("[delete_CallerUsage]CallerUseage={},deleteCount={}", item, deleteCount);
        }

        //获取所有函数调用关系
        CallerFunctionUseage queryCallerFunctionUseage = new CallerFunctionUseage();
        queryCallerFunctionUseage.setSid(service.getId());
        List<CallerFunctionUseage> callerFuncUsagePos = callerFuncUsageDao.selectPage(queryCallerFunctionUseage, PageRequest.of(0, Integer.MAX_VALUE));

        //删除所有函数调用关系
        for (CallerFunctionUseage callerFuncUsagePo : callerFuncUsagePos) {

            int deleteCount = callerFuncUsageDao.deleteByPrimaryKey(callerFuncUsagePo.getId());
            logger.info("[deleteCallerFunctionUsage]callerFuncUsagePo={},deleteCount={}", callerFuncUsagePo, deleteCount);
        }

        //删除所有相关的熔断信息
        List<CircuitBreakConfig> circuitBreakConfigPos = circuitBreakConfigDao.queryCircuitBreakConfigs(null, service.getId(), null);
        for (CircuitBreakConfig circuitBreakConfigPo : circuitBreakConfigPos) {
            int deleteCount = circuitBreakConfigDao.deleteById(circuitBreakConfigPo.getId());
            logger.info("[delete_CircuitBreakConfigPo]circuitBreakConfigPo={},deleteCount={}", circuitBreakConfigPo, deleteCount);
        }
        List<CircuitBreakEvent> circuitBreakEventPos = circuitBreakEventDao.queryCircuitBreakEvents(null, service.getId(), null, null, PageRequest.of(0, Integer.MAX_VALUE));
        for (CircuitBreakEvent circuitBreakEventPo : circuitBreakEventPos) {
            int deleteCount = circuitBreakEventDao.deleteById(circuitBreakEventPo.getId());
            logger.info("[delete_circuitBreakEventPo]circuitBreakEventPo={},deleteCount={}", circuitBreakEventPo, deleteCount);
        }
        List<CircuitBreakMonitor> circuitBreakMonitorPos = circuitBreakMonitorDao.queryCircuitBreakMonitors(null, service.getId(), null, PageRequest.of(0, Integer.MAX_VALUE));
        for (CircuitBreakMonitor circuitBreakMonitorPo : circuitBreakMonitorPos) {
            int deleteCount = circuitBreakMonitorDao.deleteById(circuitBreakMonitorPo.getId());
            logger.info("[delete_circuitBreakMonitorPo]circuitBreakMonitorPo={},deleteCount={}", circuitBreakMonitorPo, deleteCount);
        }

        //删除调用关系扩展表中数据
        List<CallerUsageExt> callerUsageExtPos = callerUsageExtDao.queryByCidOrSid(null, service.getId());
        for (CallerUsageExt callerUsageExtPo : callerUsageExtPos) {
            int deleteCount = callerUsageExtDao.deleteById(callerUsageExtPo.getId());
            logger.info("[delete_callerUsageExtPo]callerUsageExtPo={},deleteCount={}", callerUsageExtPo, deleteCount);
        }

        //删除所有分组节点信息和分组信息
        List<Group> groups = groupDaoy.selectBySid(service.getId());
        for (Group group : groups) {
            List<ServiceNodeGroup> serviceNodeGroups = serviceNodeGroupDao.selectByGid(group.getId());
            for (ServiceNodeGroup serviceNodeGroup : serviceNodeGroups) {
                int deleteCount = serviceNodeGroupDao.deleteByGid(serviceNodeGroup.getGid());
                logger.info("[delete_serviceNodeGroup]serviceNodeGroup={},deleteCount={}", serviceNodeGroup, deleteCount);
            }
            int deleteCount = groupDaoy.delete(group.getId());
            logger.info("[delete_group]group={},deleteCount={}", group, deleteCount);
        }

        //删除配置信息
        int deleteConfigCount = serviceConfigDao.deleteBySid(service.getId());
        logger.info("[delete_config]deleteConfigCount={}", deleteConfigCount);

        //获取所有服务函数
        List<ServiceFunction> serviceFunctions = serviceFunctionDao.getServiceFunctionsBySid(service.getId());

        //删除所有服务函数
        for (ServiceFunction serviceFunction : serviceFunctions) {
            int deleteCount = serviceFunctionDao.deleteById(serviceFunction.getId());
            logger.info("[delete_serviceFunction]serviceFunction={},deleteCount={}", serviceFunction, deleteCount);
        }

        //删除所有函数分组
        List<ServiceFunctionGroup> serviceFunctionGroups = serviceFunctionGroupDaoy.selectBySid(service.getId());
        for (ServiceFunctionGroup serviceFunctionGroup : serviceFunctionGroups) {
            int deleteCount = serviceFunctionGroupDaoy.deleteById(serviceFunctionGroup.getId());
            logger.info("[delete_serviceFunctionGroup]serviceFunctionGroup={},deleteCount={}", serviceFunctionGroup, deleteCount);
        }

        logger.info("op=end_deleteServiceComplete,service={}", service);
    }

}
