package com.bin.webmonitor.service;

import com.bin.webmonitor.command.ConfigChangeCommand;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.component.SendCommand;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.environment.EnvManager;
import com.bin.webmonitor.naming.ServiceNodes;
import com.bin.webmonitor.model.ServiceGroup;
import com.bin.webmonitor.model.vo.GrayNodeVO;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerSpecialDao;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.GroupDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.dao.ServiceNodeGroupDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerSpecialAttr;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.repository.domain.ServiceNodeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CoreService {

    private static Logger logger = LoggerFactory.getLogger(CoreService.class);

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private ServiceFunctionDao serviceFunctionDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private CallerUsageDao callerUsageDao;

    @Autowired
    private ServiceNodeGroupDao serviceNodeGroupDao;


    @Autowired
    private CallerSpecialDao callerSpecialDao;




    @Autowired
    private LocalCaller localCaller;

    @Autowired
    private LocalService localService;

    
    @Autowired
    private EnvManager envManager;

    @Autowired
    private SendCommand sendCommand;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private NamingProxy namingProxy;

    @Autowired
    private ServiceNodes serviceNodes;




    @Transactional
    public ApiResult<Boolean> register(ServiceInstance service) {

        ServiceInstance dbService = serviceDao.selectByName(service.getServiceName());
        if (dbService != null) {
            return new ApiResult<>(Constants.NOT_SUCCESS, "已存在服务名", "serviceName");
        }
        try {

            serviceDao.insert(service);
        } catch (DuplicateKeyException e) {
            return new ApiResult<>(Constants.NOT_SUCCESS, "已存在服务名", "serviceName");
        }
        try {
            ServiceInstance serviceDb = serviceDao.selectByName(service.getServiceName());
            groupDao.insert(new Group().setSid(serviceDb.getId()).setGroupName("默认组"));
        } catch (DuplicateKeyException e) {

        }
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    public ApiResult<Boolean> edit(ServiceInstance service) {

        ServiceInstance dbService = serviceDao.selectById(service.getId());
        if (dbService == null || !dbService.getServiceName().equals(service.getServiceName())) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("非法请求");
        }
        if (service.getTcpPort() != dbService.getTcpPort()) {
            List<ServiceNode> snodes = namingProxy.getNodes(service.getServiceName()).checkAndReturn();
            if (!CollectionUtil.isEmpty(snodes)) {
                if (snodes.stream().anyMatch(i -> i.getPort() == dbService.getTcpPort())) {
                    return new ApiResult<>(Constants.NOT_SUCCESS, "该服务已经有这个port的节点，请先删除这些节点重试", "tcpPort");
                }
            }
        }

        try {

            serviceDao.updateService(service);
        } catch (DuplicateKeyException e) {
            return new ApiResult<>(Constants.NOT_SUCCESS, "已存在服务名", "serviceName");
        }
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }



    public ServiceInstance getById(int id) {
        ServiceInstance service = serviceDao.selectById(id);
        return service;
    }

    public List<ServiceFunction> getFunctions(int sid) {
        return serviceFunctionDao.getServiceFunctionsBySid(sid);
    }

    public List<Group> getGroups(int sid) {
        return groupDao.selectBySid(sid);
    }

    public ApiResult<Boolean> deleteService(ServiceInstance service) {

        ServiceInstance serviceDb = serviceDao.selectByName(service.getServiceName());
        if (serviceDb == null) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("不存在服务名" + service.getServiceName());
        }

        List<CallerUseage> callerUsages = callerUsageDao.selectBySid(serviceDb.getId());

        if (!CollectionUtil.isEmpty(callerUsages)) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("存在调用关系，不能删除");
        }


        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(serviceDao.deleteService(serviceDb.getId()));
    }

    public ApiResult<Boolean> addGroup(ServiceInstance service, String groupName, String ips) {
        Group group = new Group().setGroupName(groupName).setSid(service.getId());
        int gid = 0;
        try {
            gid = groupDao.insert(group);
        } catch (DuplicateKeyException e) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("组名" + groupName + "已被使用").setField("groupName");
        }
        int finalGid = gid;
        String addRes = addServiceNodeGroup(service.getServiceName(), finalGid, ips);
        if (Objects.nonNull(addRes)) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage(addRes).setResult(false);
        }
        operateRecordService.addOperateRecord(null, service.getId(), OperateRecordType.SERVICE_NODEGROUP_ADD, "添加分组:" + groupName);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    public ApiResult<Boolean> updateGroup(ServiceInstance service, int gid, String groupName, String ips) {
        Group group = groupDao.selectById(gid);
        if (group == null || group.getSid() != service.getId()) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("gid参数错误");
        }

        List<ServiceNodeGroup> serviceNodeGroups = serviceNodeGroupDao.selectByGid(gid);
        if (serviceNodeGroups == null) {
            String addRes = addServiceNodeGroup(service.getServiceName(), gid, ips);
            if (Objects.nonNull(addRes)) {
                return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage(addRes).setResult(false);
            }
        } else {

            Set<String> formIps = new HashSet<>();
            Collections.addAll(formIps, ips.split(","));
            List<ServiceNodeGroup> delete = new ArrayList<>(10);
            for (ServiceNodeGroup serviceNodeGroup : serviceNodeGroups) {
                if (!formIps.contains(serviceNodeGroup.getIp())) {
                    delete.add(serviceNodeGroup);
                } else {
                    formIps.remove(serviceNodeGroup.getIp());
                }
            }

            if (delete.size() == 0 && formIps.size() == 0) {
                logger.info("service {} group {} node no chage", service.getServiceName(), groupName);
                return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
            }
            groupDao.updateGroupTime(gid);
            logger.info("service {} group {} delete ips {}", service.getServiceName(), groupName, delete);
            logger.info("service {} group {} add ips {}", service.getServiceName(), groupName, formIps);
            String addRes = addServiceNodeGroup(service.getServiceName(), gid, DelimiterHelper.join(formIps));
            if (Objects.nonNull(addRes)) {
                return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage(addRes).setResult(false);
            }

            serviceNodeGroupDao.deleteBatch(delete);

            sendConfigChange(service, gid);
        }

        logger.info("op=end_updateGroup,service={},gid={},groupName={},ips={}",  service, gid, groupName, ips);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    public ApiResult<Boolean> updateGroupStatus(int gid, int status) {
        Group group = groupDao.selectById(gid);
        if (Objects.isNull(group) || group.getStatus() == status) { // 状态一致，不用再更新了
            logger.warn("[WARN-failed-update-status]gid={},status={},group={}", gid, status, group);
            return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(false);
        }

        // 1.更新库表状态
        groupDao.updateStatus(gid, status);

        // 2.发送变更通知
        ServiceInstance service = localService.getById(group.getSid());
        sendConfigChange(service, null);

        logger.info("op=end_updateGroupStatus,gid={},status={}", gid, status);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    /**
     * 向服务的调用方发送配置变更指令
     *
     * @param service
     * @param gid     null表示不限
     */
    public void sendConfigChange(ServiceInstance service,  Integer gid) {
        logger.info("op=start_sendConfigChange,service={},gid={}", service, gid);

        List<CallerUseage> callerUseages = Objects.isNull(gid) ? callerUsageDao.selectBySid(service.getId()) : callerUsageDao.selectByCidAndGid(service.getId(), gid);
        if (callerUseages != null) {
            callerUseages.forEach(callerUseage -> {
                Caller caller = null;
                try {
                    caller = localCaller.getById(callerUseage.getCid());
                    if (caller != null) {
                        sendCommand.sendClient(caller.getCallerKey(), new ConfigChangeCommand().setService(service.getServiceName()));
                    }
                } catch (Exception e) {
                    logger.error("send caller {} service {} config change error ", caller == null ? callerUseage.getCid() : caller.getCallerName(), service.getServiceName(), e);
                }
            });
        }
    }

    /**
     * 分组添加结点（要求目标结点状态正常）
     *
     * @param serviceName
     * @param gid
     * @param ips
     * @return null表示添加成功；否则返回失败原因。
     */
    private String addServiceNodeGroup(String serviceName, int gid, String ips) {
        List<String> res = new ArrayList<>();

        List<ServiceNodeGroup> groupServiceNodes = Stream.of(ips.split(",")).filter(str -> {
            return !StringUtil.isEmpty(str);
        }).map(ip -> new ServiceNodeGroup().setIp(ip).setGid(gid)).collect(Collectors.toList());
        Map<String, ServiceNode> ip2ServiceNodeMap = namingProxy.getNodes(serviceName).checkAndReturn().stream().collect(Collectors.toMap(ServiceNode::getIp, Function.identity())); // 拉取控制中心的结点状态

        for (ServiceNodeGroup groupServiceNode : groupServiceNodes) { // 判断结点状态是否正常
            ServiceNode serviceNode = ip2ServiceNodeMap.get(groupServiceNode.getIp());
            if (Objects.isNull(serviceNode)) {
                res.add(String.format("控制中心不存在该结点(%s)", groupServiceNode.getIp()));
                continue;
            }


            if (envManager.isProd() && serviceNodes.isOnline(serviceNode.getSystemEnvType())) {
                res.add(String.format("非线上结点(%s)", groupServiceNode.getIp()));
                continue;
            }

            if (!serviceNode.isRunning()) {
                res.add(String.format("结点没有运行(%s)", groupServiceNode.getIp()));
                continue;
            }

            if (!serviceNode.isInService()) {
                res.add(String.format("结点被禁用(%s)", groupServiceNode.getIp()));
            }
        }
        if (!CollectionUtils.isEmpty(res)) {
            return DelimiterHelper.join(res);
        }

        serviceNodeGroupDao.batchInsert(groupServiceNodes);
        return null;
    }

    public List<ServiceGroup> groupList(String serviceName, int sid) {


        List<ServiceGroup> serviceGroups = new ArrayList<>(10);
        List<Group> groups = groupDao.selectBySid(sid);

        List<ServiceNode> allNodes = null;
        try {
            allNodes = namingProxy.getNodes(serviceName).checkAndReturn();
        } catch (Throwable e) {
            logger.error("control error service name {}", serviceName, e);
        }
        if (allNodes == null) {
            allNodes = new LinkedList<>();
        }
        for (Group group : groups) {
            ServiceGroup serviceGroup = new ServiceGroup().setGroup(group).setUpdateable(true);

            List<ServiceNodeGroup> nodes = serviceNodeGroupDao.selectByGid(group.getId());
            List<String> groupNodes = nodes.stream().map(ServiceNodeGroup::getIp).collect(Collectors.toList());

            List<String> canSelect = allNodes.stream().filter(node -> !groupNodes.contains(node.getIp())).map(ServiceNode::getIp).collect(Collectors.toList());

            serviceGroup.setCanSelectIps(canSelect);
            serviceGroup.setGroupIps(groupNodes);

            List<CallerUseage> usages = callerUsageDao.selectByCidAndGid(sid, group.getId());
            serviceGroup.setInUse(!CollectionUtils.isEmpty(usages));

            serviceGroups.add(serviceGroup);
        }

        return serviceGroups;
    }

    public ApiResult<Boolean> deleteGroup( ServiceInstance service, int gid) {
        Group group = groupDao.selectById(gid);

        if (group == null) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("gid错误");
        }
        if ("默认组".equals(group.getGroupName())) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("默认组不能删除");
        }
        logger.info("delete service {} group {} ",  service.getServiceName(), group.getGroupName());
        List<CallerUseage> callerUseages = callerUsageDao.selectByCidAndGid(service.getId(), gid);

        if (callerUseages != null && callerUseages.size() > 0) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("有调用方调用不允许删除");
        }
        groupDao.delete(gid);
        serviceNodeGroupDao.deleteByGid(gid);
        logger.info(" delete service {} group {} success",  service.getServiceName(), group.getGroupName());
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    public int maxPort() {
        int from = 16500;
        List<ServiceInstance> services = serviceDao.selectAll();
        Set<Integer> ports = services.stream().map(ServiceInstance::getTcpPort).collect(Collectors.toSet());
        for (int i = 1; i < 65536 - 16500; i++) {
            if (ports.contains(from)) {
                from++;
            } else {
                return from;
            }
        }

        return from;
    }

    public List<ServiceInstance> serviceList( Pageable pageable, ServiceInstance service) {


        List<ServiceInstance> services = serviceDao.serviceList(service, pageable);
        if (services == null) {
            return Collections.emptyList();
        }

        services.forEach(service1 -> {
            service1.setCreateTimeStr(TimeUtil.date2fullStr(service1.getCreateTime()));

        });
        return services;
    }

    public List<ServiceInstance> serviceListForTran(Pageable pageable, ServiceInstance service) {


        List<ServiceInstance> services = serviceDao.serviceListForTran(service, pageable);
        if (services == null) {
            return Collections.emptyList();
        }

        return services;
    }

    public int countServiceList(ServiceInstance service) {

        return serviceDao.countServiceList(service);
    }

    public int countServiceListForTran(ServiceInstance service) {

        return serviceDao.countServiceListForTran(service);
    }

    public List<GrayNodeVO> grayList(int sid) {
        List<CallerSpecialAttr> specials = callerSpecialDao.selectBySid(sid);
        if (CollectionUtils.isEmpty(specials)) {
            return Collections.emptyList();
        }

        Map<String, Set<String>> serverIpCallerIp = new HashMap<>();

        for (CallerSpecialAttr specialAttr : specials) {
            Caller caller = localCaller.getById(specialAttr.getCid());
            if (caller == null) {
                continue;
            }
            for (String sip : specialAttr.getAttributes().getSips().split(",")) {
                for (String cip : specialAttr.getCips().split(",")) {
                    serverIpCallerIp.computeIfAbsent(sip + "_" + caller.getCallerName(), (item) -> new TreeSet<>()).add(cip);
                }
            }
        }

        List<GrayNodeVO> grayNodeVos = new LinkedList<>();
        serverIpCallerIp.forEach((sipCaller, cips) -> {
            String[] ipCaller = sipCaller.split("_");
            String ip = ipCaller[0];
            String callerName = ipCaller[1];
            grayNodeVos.add(new GrayNodeVO().setIp(ip).setCaller(callerName).setCallerIps(DelimiterHelper.join(cips)));
        });
        return grayNodeVos;
    }


}
