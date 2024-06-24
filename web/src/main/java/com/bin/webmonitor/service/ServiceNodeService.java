package com.bin.webmonitor.service;

import com.bin.webmonitor.command.ConfigChangeCommand;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.component.SendCommand;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.model.Response;
import com.bin.webmonitor.naming.ServiceCluster;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.naming.ServiceNodeWeight;
import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.model.vo.ServiceNodeConfigVo;
import com.bin.webmonitor.model.vo.ServiceNodeVO;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.GroupDao;
import com.bin.webmonitor.repository.dao.ServiceConfigDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.dao.ServiceNodeGroupDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceConfig;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.repository.domain.ServiceNodeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Component
public class ServiceNodeService {

    private Logger logger = LoggerFactory.getLogger(ServiceNodeService.class);

    @Autowired
    private ServiceNodeGroupDao serviceNodeGroupDao;

    @Autowired
    private CallerUsageDao callerUsageDao;

    @Autowired
    private SendCommand sendCommand;

    @Autowired
    private LocalCaller localCaller;

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private LocalService localService;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ServiceConfigDao serviceConfigDao;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private CoreService coreService;
    @Autowired
    private NamingProxy namingProxy;


    public ApiResult<Boolean> delete( ServiceInstance service, String ip) {
        logger.info("delete service {} node {}",  service.getServiceName(), ip);

        // if ip state is running , return error
        List<ServiceNode> nodes = namingProxy.getNodes(service.getServiceName()).checkAndReturn();
        if (CollectionUtil.isEmpty(nodes)) {
            return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
        }
        for (ServiceNode node : nodes) {
            if (ip.equals(node.getIp())) {
                if (node.isRunning()) {
                    return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("请杀死进程后再删除");
                } else {
                    break;
                }
            }
        }

        Integer count = serviceNodeGroupDao.countBySidIp(service.getId(), ip);
        if (count == null || count < 1) {
            ServiceCluster sc = new ServiceCluster()
                    .setServiceName(service.getServiceName())
                    .setIps(List.of(ip));
            logger.info(" delete node {}",  ip);
            Response<?> res = namingProxy.batchDelete(sc);
            logger.info("delete service {} node {} res {}",  service.getServiceName(), ip, res);
            return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
        } else {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("请从节点分组中删除后重试");
        }
    }

    public DataGrid<ServiceNodeVO> list(ServiceInstance service) {
        DataGrid<ServiceNodeVO> serviceNodeVoDataGrid = new DataGrid<>();
        List<ServiceNode> nodes = namingProxy.getNodes(service.getServiceName()).checkAndReturn();
        if (CollectionUtil.isEmpty(nodes)) {
            return serviceNodeVoDataGrid;
        }
        ServiceInstance sDb = serviceDao.selectByName(service.getServiceName());

        List<ServiceNodeVO> serviceNodeVos = nodes.stream().map(node -> {
            ServiceNodeVO serviceNodeVO = new ServiceNodeVO().from(node);
            serviceNodeVO.setSid(sDb.getId());
            serviceNodeVO.setUpdate(true);
            serviceNodeVO.setIdcName(StringUtil.isEmpty(node.getIdcName()) ? "" : node.getIdcName());
            return serviceNodeVO;
        }).sorted().collect(Collectors.toList());

        serviceNodeVoDataGrid.setRows(serviceNodeVos);
        serviceNodeVoDataGrid.setTotal(serviceNodeVos.size());
        serviceNodeVoDataGrid.setCurrent(1);
        return serviceNodeVoDataGrid;
    }

    public ApiResult<Boolean> onOff( ServiceInstance service, String ip, boolean on) {
        logger.info("set service {} ip {} {}",  service.getServiceName(), ip, on);
        ServiceCluster cluster = new ServiceCluster();
        cluster.setIps(List.of(ip));
        cluster.setServiceName(service.getServiceName());
        Response<?> re = null;
        if (on) {
            re = namingProxy.batchOpen(cluster);
        } else {
            re = namingProxy.batchClose(cluster);
        }

        logger.info("set service {} ip {} {} response {}",  service.getServiceName(), ip, on, re);

        List<ServiceNodeGroup> sng = serviceNodeGroupDao.selectBySidIp(localService.getByName(service.getServiceName()).getId(), ip);

        if (!CollectionUtil.isEmpty(sng)) {
            for (ServiceNodeGroup serviceNodeGroup : sng) {
                try {
                    List<CallerUseage> cu = callerUsageDao.selectByCidAndGid(localService.getByName(service.getServiceName()).getId(), serviceNodeGroup.getGid());
                    for (CallerUseage callerUseage : cu) {
                        sendCommand.sendClient(localCaller.getById(callerUseage.getCid()).getCallerKey(), new ConfigChangeCommand().setService(service.getServiceName()));
                    }
                } catch (Throwable e) {
                    logger.warn("");
                }
            }
        }

        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    public List<String> getIps(String serviceName, int gid) {
        List<ServiceNodeGroup> nodeGroups = serviceNodeGroupDao.selectByGid(gid);
        return nodeGroups.stream().map(ServiceNodeGroup::getIp).collect(Collectors.toList());
    }

    /**
     * 获取调用关系中，服务方分组中无结点的信息
     *
     * @return
     */
    public List<String> getCallerNoServiceGroupNodeList() {
        // 1.找到所有新平台的调用关系
        List<CallerUseage> callerUsages = callerUsageDao.selectAll();

        // 2.找出找到调用方无分组结点的信息
        List<String> callerNoGroupNodeList = new ArrayList<>();
        for (CallerUseage callerUsage : callerUsages) {
            List<ServiceNodeGroup> serviceGroupNodes = serviceNodeGroupDao.selectByGid(callerUsage.getGid());

            if (CollectionUtil.isEmpty(serviceGroupNodes)) {
                Caller caller = localCaller.getById(callerUsage.getCid());
                ServiceInstance service = localService.getById(callerUsage.getSid());
                Group group = groupDao.selectById(callerUsage.getGid());
                callerNoGroupNodeList.add(String.format("%s->%s.%s", caller.getCallerName(), service.getServiceName(), group.getGroupName()));
            }
        }

        Collections.sort(callerNoGroupNodeList);
        return callerNoGroupNodeList;
    }

    public ApiResult<Boolean> updateServiceNodeConfig( ServiceNodeConfigVo serviceNodeConfigVo) {
        ServiceInstance service = localService.getById(serviceNodeConfigVo.getSid());

        // 1.更新权重
        Object response = namingProxy.updateNodeWeight(new ServiceNodeWeight(service.getServiceName(), serviceNodeConfigVo.getIp(), serviceNodeConfigVo.getNewWeight())).checkAndReturn();

        // 2.向同分组的调用方发送配置变更命令
        List<ServiceNodeGroup> serviceNodeGroups = serviceNodeGroupDao.selectBySidIp(service.getId(), serviceNodeConfigVo.getIp());
        if (!CollectionUtil.isEmpty(serviceNodeGroups)) {
            for (ServiceNodeGroup serviceNodeGroup : serviceNodeGroups) {
                coreService.sendConfigChange(service, serviceNodeGroup.getGid());
            }
        }

        operateRecordService.addOperateRecord(null, service.getId(), OperateRecordType.SERVICE_NODE_UPDATE, String.format("修改服务节点%s的权重，旧值：%d，新值：%d", serviceNodeConfigVo.getIp(), serviceNodeConfigVo.getOriginWeight(), serviceNodeConfigVo.getNewWeight()));
        logger.info("op=end_updateServiceNodeConfig,serviceNodeConfigVo={},response={},serviceNodeGroups={}", serviceNodeConfigVo, response, serviceNodeGroups);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS);
    }

    public ServiceConfig getServiceConfig(String serviceName, String ip) {
        ServiceInstance service = localService.getByName(serviceName);
        ServiceConfig serviceConfig = serviceConfigDao.selectServiceConfigBySidAndIp(service.getId(), ip);
        if (Objects.isNull(serviceConfig)) {
            logger.info("op=getServiceconfig, config of service:{} and ip={} do not exists", service, ip);
            String configMsg = "配置信息不存在。\r\n 可能因为绑定服务服务平台host问题，不能有效上传配置信息。";
            return new ServiceConfig(service.getId(), ip, configMsg, configMsg);
        }

        serviceConfig.setUpdateTimeStr(TimeUtil.date2fullStr(serviceConfig.getUpdateTime()));
        String serviceLog4j = serviceConfig.getLog4j();
        serviceLog4j = StringUtil.isEmpty(serviceLog4j) ? "无" : serviceLog4j.replace("<", "&lt;").replace(">", "&gt;");
        serviceConfig.setLog4j(serviceLog4j);

        String config = serviceConfig.getConfig();
        config = StringUtil.isEmpty(config) ? "无" : config.replace("<", "&lt;").replace(">", "&gt;");
        serviceConfig.setConfig(config);

        return serviceConfig;
    }

    /**
     * 将节点上的服务移出分组
     *
     * @param serviceName 服务名
     * @param ip 节点ip
     * @return 是否成功
     */
    public ServiceResult<Boolean> removeNodeService(String serviceName, String ip) {
        ServiceInstance service = serviceDao.selectByName(serviceName);
        if (Objects.isNull(service)) {
            logger.info("op=removeNodeService,service_not_exist,serviceName={}", serviceName);
            return ServiceResult.noService();
        }

        List<ServiceNodeGroup> serviceNodeGroups = serviceNodeGroupDao.selectBySidIp(service.getId(), ip);
        serviceNodeGroupDao.deleteBatch(serviceNodeGroups);

        coreService.sendConfigChange(service, null);

        Map<String, List<ServiceNodeGroup>> res = new HashMap<>();
        List<ServiceNodeGroup> afterServiceNodeGroups = serviceNodeGroupDao.selectBySidIp(service.getId(), ip);
        res.put("beforeDelete", serviceNodeGroups);
        res.put("afterDelete", afterServiceNodeGroups);
        logger.info("op=end_removeNodeService,res={}", res);

        return new ServiceResult<>(ServiceResult.SUCCESS, true);

    }

    /**
     *  移除节点上的所有服务（将所有服务的该节点移除分组）
     *
     * @param ip 节点ip
     * @return 是否成功
     */
    public ServiceResult<Boolean> removeAllService(String ip) {
        Map<String, Object> res = new HashMap<>();

        List<ServiceNodeGroup> serviceNodeGroups = serviceNodeGroupDao.selectByIp(ip);
        serviceNodeGroupDao.deleteBatch(serviceNodeGroups);

        // 发送配置变更指令
        for (ServiceNodeGroup serviceNodeGroup : serviceNodeGroups) {
            Group group = groupDao.selectById(serviceNodeGroup.getGid());
            if (Objects.nonNull(group)) {
                ServiceInstance service = localService.getById(group.getSid());
                coreService.sendConfigChange(service, null);
            }
        }

        List<ServiceNodeGroup> afterDeleteServiceNode = serviceNodeGroupDao.selectByIp(ip);
        res.put("beforeDelete", serviceNodeGroups);
        res.put("afterDelete", afterDeleteServiceNode);
        logger.info("op=end_removeAllService,res={}", res);

        return new ServiceResult<>(ServiceResult.SUCCESS, true);
    }


}
