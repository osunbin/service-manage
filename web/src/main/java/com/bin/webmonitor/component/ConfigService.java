package com.bin.webmonitor.component;


import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.JsonHelper.JsonParse;
import com.bin.webmonitor.common.MethodKey;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.naming.ServiceNodes;
import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.naming.model.Status;
import com.bin.webmonitor.naming.model.Statuses;
import com.bin.webmonitor.enums.CallerSpecialAttributes;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta;
import com.bin.webmonitor.model.CallerCountMeta;
import com.bin.webmonitor.model.CallerDegradeMeta;
import com.bin.webmonitor.model.ServerNode;
import com.bin.webmonitor.model.ServiceCountMeta;
import com.bin.webmonitor.model.ServiceFunctions;
import com.bin.webmonitor.model.ServiceRejectMeta;
import com.bin.webmonitor.model.ServerConfig;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalFunction;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.CallerSpecialDao;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.CallerUsageExtDao;
import com.bin.webmonitor.repository.dao.CircuitBreakConfigDao;
import com.bin.webmonitor.repository.dao.ClientConfigDao;
import com.bin.webmonitor.repository.dao.GroupDao;
import com.bin.webmonitor.repository.dao.ServiceConfigDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.dao.ServiceNodeGroupDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.CallerSpecialAttr;
import com.bin.webmonitor.repository.domain.CallerUsageExt;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.CircuitBreakConfig;
import com.bin.webmonitor.repository.domain.ClientConfig;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceConfig;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.repository.domain.ServiceNodeGroup;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 1、根据callerKey和service客户端获取配置信息
 * 2、根据callerKey和service客户端判断是否使用新的服务管理平台
 * 3、根据serviceName获取函数id map以及调用方callerkey id的map
 * 4、根据callerkey，service获取调用service id，函数ip map
 * 5、根据callerkey和service获取降级信息
 * 6、根据(callerkey,service,method)获取熔断信息
 */
@SuppressWarnings("all")
@Component
public class ConfigService {

    private static Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    private LocalCaller localCaller;

    @Autowired
    private LocalService localService;

    @Autowired
    private CallerSpecialDao specialDao;

    @Autowired
    private CallerUsageDao callerUsageDao;

    @Autowired
    private CallerUsageExtDao callerUsageExtDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ServiceNodeGroupDao svcNodeGroupDao;

    @Autowired
    private ServiceFunctionDao serviceFunctionDao;

    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;

    @Autowired
    private LocalFunction localFunction;



    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private CircuitBreakConfigDao circuitBreakConfigDao;

    @Autowired
    private ServiceConfigDao serviceConfigDao;

    @Autowired
    private ClientConfigDao clientConfigDao;


    @Autowired
    private NamingProxy namingProxy;

    @Autowired
    private ServiceNodes serviceNodes;

    public ServiceResult<ServerConfig> getConfig(String callerKey, String serviceName, String callerIp, long clientConfigTime) {
        logger.info("op=start_getConfig,callerKey={},serviceName={},callerIp={},clientConfigTime={}", callerKey, serviceName, callerIp, clientConfigTime);
        ServiceResult<ServerConfig> resultRe = null;

        Caller caller = localCaller.getByCallerKey(callerKey);
        if (caller == null) {
            logger.warn("no caller key {}", callerKey);
            return ServiceResult.noCaller(callerKey);
        }

        ServiceInstance service = serviceDao.selectByName(serviceName);
        if (service == null) {
            logger.warn("no service by service name  {}", serviceName);
            return ServiceResult.noService(serviceName);
        }

        CallerUseage callerUseage = callerUsageDao.selectByCidSid(caller.getId(), service.getId());
        // 更新调用关系扩展信息
        updateCallerUsageExtInfo(caller.getId(), service.getId(), callerUseage, callerIp);
        if (null == callerUseage) {
            logger.warn("callerUseage must be not null. caller:" + caller.getCallerName() + " service:" + serviceName + " -fromIP:" + callerIp);
            return new ServiceResult<>(new Status(Statuses.NO_CALLER_USEAGE, "没有调用关系"));
        }

        if (callerUseage.isReject()) {
            logger.warn("callerUseage status is forbid. caller:" + caller.getCallerName() + " service:" + serviceName + " -fromIP:" + callerIp);
            return new ServiceResult<>(new Status(Statuses.FORBID, "服务" + serviceName + "禁止调用"));
        }

        long callerUseageTime = callerUseage.getUpdateTime().getTime();
        long gid = callerUseage.getGid();
        Group group = groupDao.selectById(gid);

        if (group == null) {
            logger.warn("callerUseage no group . caller:" + caller.getCallerName() + " service:" + serviceName + " -fromIP:" + callerIp);
            return new ServiceResult<>(new Status(Statuses.NO_GROUP, "服务" + serviceName + "不存在分组id:" + gid));
        }

        List<ServiceNode> allControlNodes = new ArrayList<>();
        try {
            allControlNodes = namingProxy.getNodes(service.getServiceName()).checkAndReturn();
        } catch (Exception e) {
            logger.error("service {} get nodes from control error", service.getServiceName(), e);
        }

        long groupTime = group.getUpdateTime().getTime();

        List<ServiceNodeGroup> nodes = svcNodeGroupDao.selectByGid(gid); // 物理分组
        if (CollectionUtil.isEmpty(nodes)) {
            resultRe = new ServiceResult<ServerConfig>(new Status(Statuses.SERVICE_NO_NODES, "服务没有节点"));
            logger.info("[getConfig_no_nodes]op=end_getConfig,callerKey={},serviceName={},callerIp={},clientConfigTime={},allControlNodes={},dbNodes={},res={}", callerKey, serviceName, callerIp, clientConfigTime, allControlNodes, nodes, resultRe);
            return resultRe;
        }

        List<String> availableNodes = filterAvailableNodes(nodes, allControlNodes);
        if (CollectionUtil.isEmpty(availableNodes)) {
            resultRe = new ServiceResult<ServerConfig>(new Status(Statuses.SERVICE_NO_NODES_AFTER_CONTROL_FILTER, "控制中心没有可用节点"));
            logger.info("[getConfig_no_avail_nodes]op=end_getConfig,callerKey={},serviceName={},callerIp={},clientConfigTime={},allControlNodes={},dbNodes={},res={}", callerKey, serviceName, callerIp, clientConfigTime, allControlNodes, nodes, resultRe);
            return resultRe;
        }

        Map<String, ServiceNode> ip2ServiceNodeMap = allControlNodes.stream().collect(Collectors.toMap(ServiceNode::getIp, Function.identity()));
        String result = createConfig(service, availableNodes, callerUseage, callerIp, ip2ServiceNodeMap);
        resultRe = new ServiceResult<ServerConfig>(ServiceResult.SUCCESS, new ServerConfig(result, null).setChangeTime(Math.max(callerUseageTime, groupTime)));

        logger.info("op=end_getConfig,callerKey={},serviceName={},callerIp={},clientConfigTime={},allControlNodes={},dbNodes={},availableNodes={},resultRe={}", callerKey, serviceName, callerIp, clientConfigTime, allControlNodes, nodes, availableNodes, resultRe);
        return resultRe;
    }

    private List<String> filterAvailableNodes(List<ServiceNodeGroup> nodes, List<ServiceNode> allControlNodes) {
        // 获取控制中心的可用节点
        Set<String> availableControlNodes = allControlNodes.stream().filter(node -> node.isInService()).map(ServiceNode::getIp).collect(Collectors.toSet());

        return nodes.stream().filter(node -> availableControlNodes.contains(node.getIp())).map(ServiceNodeGroup::getIp).collect(Collectors.toList());
    }

    /**
     * {
     *     Service:{
     *
     *         SocketPool:{
     *
     *         },
     *         Server:{
     *             deadTimeout:1,
     *             nodes:[
     *             ]
     *         },
     *         function:[{
     *             methodKey:,
     *             timeout
     *         }]
     *     }
     * }
     */
    private String createConfig(ServiceInstance service, List<String> srvNodeList, CallerUseage callerUseage, String callerIp, Map<String, ServiceNode> ip2ServiceNodeMap) {

        try {

            boolean isSpecialCallerIp = false;
            String specialSerializeType = null;
            List<String> specialServerIps = new ArrayList<String>();

            List<CallerSpecialAttr> specialAttrList = specialDao.selectByCidAndSid(callerUseage.getSid(), callerUseage.getCid());
            if (null != specialAttrList && specialAttrList.size() > 0) {
                callerIp = callerIp.trim();
                for (CallerSpecialAttr specialAttr : specialAttrList) {
                    String[] callerIps = specialAttr.getCips().split(",");
                    for (int i = 0; i < callerIps.length; i++) {
                        if (callerIp.equals(callerIps[i].trim())) {
                            String attributes = specialAttr.getAttrJson();
                            try {
                                JsonParse jsonParse = new JsonParse(attributes);
                                specialSerializeType = jsonParse.getStr(CallerSpecialAttributes.SerializeVersion.getKey());
                                String strIps = jsonParse.getStr(CallerSpecialAttributes.ServerIps.getKey());

                                if (StringUtil.isNotEmpty(strIps)) {
                                    String[] sIps = strIps.split(",");
                                    for (String serverIp : sIps) {
                                        specialServerIps.add(serverIp);
                                    }
                                }
                                isSpecialCallerIp = true;
                                break;
                            } catch (Exception e) {
                                logger.error("", e);
                            }
                        }
                    }
                    if (isSpecialCallerIp) {
                        break;
                    }
                }
            }



            Map<String,Object> protocolConfig = new HashMap<>();
            if (isSpecialCallerIp && StringUtil.isNotEmpty(specialSerializeType)) {
                protocolConfig.put("serialize",specialSerializeType);
            } else {
                protocolConfig.put("serialize",callerUseage.getProtocolSerialize());
            }
            protocolConfig.put("compressType",callerUseage.getProtocolCompressType());
            protocolConfig.put("protocolType",callerUseage.getProtocolProtocolType());



            List<Map<String,Object>> serverListConfigs = new ArrayList<>();
            if (isSpecialCallerIp && specialServerIps.size() > 0) {
                logger.info(" service {} user gray config ", service.getServiceName());
                for (int i = 0; i < specialServerIps.size(); i++) {
                    String serverIp = specialServerIps.get(i);
                    if (StringUtil.isEmpty(serverIp)) {
                        continue;
                    }

                    ServiceNode serviceNode = ip2ServiceNodeMap.get(serverIp);
                    if (Objects.isNull(serviceNode)) {
                        logger.warn("[warn_createConfig_no_service_node]ip={}", serverIp);
                        continue;
                    }
                    if (service.getTcpPort() != serviceNode.getPort()) {
                        logger.warn("[warn_createConfig_diff_port]ServicePort={},namingServicePort={}", service.getTcpPort(), serviceNode.getPort());
                        continue;
                    }
                    Map<String,Object> serverListConfig = new HashMap<>();
                    serverListConfig.put("name",service.getServiceName() + i);
                    serverListConfig.put("host",serverIp);
                    serverListConfig.put("port",service.getTcpPort());
                    serverListConfig.put("version",serviceNode.getContainerVersion());
                    serverListConfig.put("weight",serviceNode.getWeight());
                    serverListConfig.put("systemEnv",serviceNode.getSystemEnvType());
                    serverListConfig.put("idc",serviceNode.getIdcName());
                    serverListConfigs.add(serverListConfig);
                }
            } else {
                int nodeIndex = 0;


                for (String srvNode : srvNodeList) {
                    if (StringUtil.isEmpty(srvNode)) {
                        continue;
                    }

                    ServiceNode serviceNode = ip2ServiceNodeMap.get(srvNode);
                    if (Objects.isNull(serviceNode)) {
                        logger.warn("[warn_createConfig_no_service_node]ip={}", srvNode);
                        continue;
                    }
                    if (service.getTcpPort() != serviceNode.getPort()) {
                        logger.warn("[warn_createConfig_diff_port]ServicePort={},namingServicePort={}", service.getTcpPort(), serviceNode.getPort());
                        continue;
                    }
                    Map<String,Object> serverListConfig = new HashMap<>();
                    serverListConfig.put("name",service.getServiceName() + (nodeIndex++));
                    serverListConfig.put("host",srvNode);
                    serverListConfig.put("port",service.getTcpPort());
                    serverListConfig.put("version",serviceNode.getContainerVersion());
                    serverListConfig.put("weight",serviceNode.getWeight());
                    serverListConfig.put("systemEnv",serviceNode.getSystemEnvType());
                    serverListConfig.put("idc",serviceNode.getIdcName());
                    serverListConfigs.add(serverListConfig);

                }
            }




            List<Map<String,Object>> functions = new ArrayList<>();
            List<CallerFunctionUseage> callerFunctionUseages = callerFuncUsageDao.selectByCidAndSid(callerUseage.getCid(), callerUseage.getSid()).stream().filter(callerFuncUsage -> callerFuncUsage.getTimeout() > 0).collect(Collectors.toList());
            for (CallerFunctionUseage callerFunctionUseage : callerFunctionUseages) {
                String methodKey = serviceFunctionDao.getServiceFunctionsByid(callerFunctionUseage.getFid()).getFname();

                Map<String,Object> methodConfig = new HashMap<>();
                methodConfig.put("methodKey",methodKey);
                methodConfig.put("timeout",callerFunctionUseage.getTimeout());
                functions.add(methodConfig);
            }



            Map<String,Object> serverConfig = new HashMap<>();
            // 服务器挂后心跳检测间隔时间
            serverConfig.put("deadTimeout",callerUseage.getServerDeadTimeout());
            serverConfig.put("list",serverListConfigs);

            Map<String,Object> serviceConfig = new HashMap<>();
            serviceConfig.put("name",service.getServiceName());
            serviceConfig.put("sid",service.getId() & 127);

            serviceConfig.put("protocol",protocolConfig);

            serviceConfig.put("server",serverConfig);
            serviceConfig.put("function",functions);

            return JsonHelper.toJson(serviceConfig);
        } catch (Exception e) {
            logger.error("create config error.", e);
            throw e;
        }
    }

    /**
     * 获取所有分组内可用的云节点
     *
     * @param serviceName
     * @return
     */
    public ServiceResult<List<ServerNode>> getAllAvailableCloudNodesInGroup(String serviceName) {
        ServiceInstance service = localService.getByName(serviceName);
        if (Objects.isNull(service)) {
            logger.warn("no service by service name  {}", service);
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "no service by name " + serviceName));
        }

        // 1.从控制中心获取所有节点列表
        Map<String, ServiceNode> ip2ServiceNodeMap = namingProxy.getNodes(serviceName).checkAndReturn().stream().collect(Collectors.toMap(ServiceNode::getIp, Function.identity()));
        // 2.从DB获取所有分组内的节点
        List<ServiceNodeGroup> dbServiceNodes = svcNodeGroupDao.selectBySid(service.getId());
        List<ServiceNode> availableCloudNodes = new ArrayList<>();

        // 3.过滤出所有分组内可用的云节点
        for (ServiceNodeGroup dbServiceNode : dbServiceNodes) {// 逐个判断分组内的节点
            ServiceNode serviceNode = ip2ServiceNodeMap.get(dbServiceNode.getIp());// 获取控制中心的云节点信息
            if (Objects.isNull(serviceNode)) {// 不存在，则跳过
                logger.warn("[NULL_serviceNode]dbServiceNode={}", dbServiceNode);
                continue;
            }


            if (serviceNodes.isDocker(serviceNode.getServerType()) && serviceNode.isInService() && serviceNode.isRunning()) {
                availableCloudNodes.add(serviceNode);
            }

        }

        // 4.转换
        List<ServerNode> serverNodes = new ArrayList<>(availableCloudNodes.size());
        for (int i = 0; i < availableCloudNodes.size(); i++) {
            ServiceNode cloudNode = availableCloudNodes.get(i);
            serverNodes.add(new ServerNode(service.getServiceName() + "-cloud" + i, cloudNode.getIp(), service.getTcpPort(), cloudNode.getContainerVersion(), cloudNode.getWeight()));
        }

        return new ServiceResult<>(ServiceResult.SUCCESS, serverNodes);
    }

    /**
     * 更新调用关系扩展信息
     * @param cid
     * @param sid
     * @param callerUseage
     * @param callerIp
     */
    private void updateCallerUsageExtInfo(int cid, int sid, CallerUseage callerUseage, String callerIp) {
        try {
            logger.info("op=start_updateCallerUsageExtInfo,cid={},sid={},callerUseage={},callerIp={}", cid, sid, callerUseage, callerIp);
            // 忽略办公网的调用
            if (StringUtil.startsWith(callerIp, "10.242")) {
                logger.warn("[WARN_ignore_office_caller_ip]cid={},sid={},callerUseage={},callerIp={}", cid, sid, callerUseage, callerIp);
                return;
            }

            CallerUsageExt callerUsageExtPo = new CallerUsageExt().setCid(cid).setSid(sid);
            callerUsageExtPo.setConfigType(Objects.isNull(callerUseage) ? CallerUsageExt.CONFIG_TYPE_LOCAL : CallerUsageExt.CONFIG_TYPE_SRVMGR);

            CallerUsageExt dbCallerUsageExtPo = callerUsageExtDao.queryByCidSid(cid, sid);
            int count = 0;
            if (Objects.isNull(dbCallerUsageExtPo)) {
                count = callerUsageExtDao.save(callerUsageExtPo);
            } else {
                count = callerUsageExtDao.updateByCidSid(callerUsageExtPo);
            }

            logger.info("op=end_updateCallerUsageExtInfo,cid={},sid={},callerUseage={},dbCallerUsageExtPo={},callerUsageExtPo={},count={}", cid, sid, callerUseage, dbCallerUsageExtPo, callerUsageExtPo, count);
        } catch (Exception e) {
            // 正常不会出错，但如果出错不能影响正常请求
            logger.error("[ERROR_updateCallerUsageExtInfo]cid={},sid={},callerUseage={}", cid, sid, callerUseage, e);
        }
    }

    public ServiceResult<ServiceCountMeta> getServiceCountMeta(String serviceName) {

        ServiceInstance service = localService.getByName(serviceName);
        if (service == null) {
            logger.warn("no service by service name  {}", service);
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "no service by name " + serviceName));
        }

        List<ServiceFunction> serviceFunctions = serviceFunctionDao.getServiceFunctionsBySid(service.getId());
        if (serviceFunctions == null) {
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE_FUNCTION, "service " + serviceName + " no function"));
        }

        List<CallerUseage> callerUsages = callerUsageDao.selectBySid(service.getId());

        ConcurrentHashMap<String, Integer> functionIdMap = new ConcurrentHashMap<>();
        serviceFunctions.forEach(i -> functionIdMap.put(i.getFname(), i.getId()));

        ConcurrentHashMap<String, Integer> callerKeyIdMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> callerKeyCallerNameMap = new ConcurrentHashMap<>();

        if (callerUsages != null) {
            callerUsages.forEach(callerUseage -> {
                try {
                    Caller caller = localCaller.getById(callerUseage.getCid());
                    if (caller != null) {
                        callerKeyIdMap.put(caller.getCallerKey(), caller.getId());
                        callerKeyCallerNameMap.put(caller.getCallerKey(), caller.getCallerName());
                    } else {
                        logger.warn("no caller by id {}", callerUseage.getCid());
                    }
                } catch (Throwable e) {
                    logger.error("get caller error by id {}", callerUseage.getCid());
                }
            });
        }

        ServiceCountMeta serviceCountMeta = new ServiceCountMeta();
        serviceCountMeta.setFunctionIdMap(functionIdMap);
        serviceCountMeta.setCallerKeyIdMap(callerKeyIdMap);
        serviceCountMeta.setCallerKeyCallerNameMap(callerKeyCallerNameMap);
        return new ServiceResult<>(ServiceResult.SUCCESS, serviceCountMeta);
    }

    public ServiceResult<?> getCallerCountMeta(String serviceName) {
        ServiceInstance service = localService.getByName(serviceName);
        if (service == null) {
            logger.warn("no service by service name  {}", service);
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "no service by name " + serviceName));
        }

        List<ServiceFunction> serviceFunctions = serviceFunctionDao.getServiceFunctionsBySid(service.getId());
        if (serviceFunctions == null) {
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE_FUNCTION, "service " + serviceName + " no function"));
        }
        ConcurrentHashMap<String, Integer> functionIdMap = new ConcurrentHashMap<>();
        serviceFunctions.forEach(i -> functionIdMap.put(i.getFname(), i.getId()));
        return new ServiceResult<>(ServiceResult.SUCCESS, new CallerCountMeta().setServiceName(serviceName).setServiceId(service.getId()).setFunctionIdMap(functionIdMap));
    }

    public ServiceResult<CallerDegradeMeta> getCallerDegradeMeta(String callerKey, String serviceName) {
        Caller caller = localCaller.getByCallerKey(callerKey);
        if (caller == null) {
            logger.warn("no caller key {}", callerKey);
            return new ServiceResult<>(new Status(Statuses.NO_CALLER, "no caller by callerKey " + callerKey));
        }

        ServiceInstance service = localService.getByName(serviceName);
        if (service == null) {
            logger.warn("no service by service name  {}", service);
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "no service by name " + serviceName));
        }

        boolean degrade = callerUsageDao.getDegrade(caller.getId(), service.getId());
        List<CallerFunctionUseage> callerFunctionUseages = callerFuncUsageDao.selectByCidAndSid(caller.getId(), service.getId());

        CallerDegradeMeta callerDegradeMeta = new CallerDegradeMeta().setService(serviceName).setDegrade(degrade);
        if (callerFunctionUseages != null) {
            Set<String> degradeFunctions = new HashSet<>();
            callerFunctionUseages.forEach(callerFunctionUseage -> {
                try {
                    if (callerFunctionUseage.isDegrade()) {
                        degradeFunctions.add(localFunction.function(callerFunctionUseage.getFid()));
                    }
                } catch (Throwable e) {
                    logger.error("get function error fid {}", callerFunctionUseage.getFid());
                }
            });
            callerDegradeMeta.setDegradeFunctions(degradeFunctions);
        }

        return new ServiceResult<>(ServiceResult.SUCCESS, callerDegradeMeta);
    }

    /**
     * 获取调用方的熔断配置信息
     *
     * @param callerKey
     * @param serviceName null表示不限
     * @param method      null表示不限
     * @return
     */
    public ServiceResult<CallerCircuitBreakConfigMeta> getCallerCircuitBreakConfigMeta(String callerKey, String serviceName, String method) {
        // 1.获取caller信息
        Caller caller = localCaller.getByCallerKey(callerKey);
        if (caller == null) {
            logger.warn("[NULL-caller]callerKey={}", callerKey);
            return new ServiceResult<>(new Status(Statuses.NO_CALLER, "no caller by callerKey " + callerKey));
        }

        // 2.获取sid
        Integer sid = null;
        if (Objects.nonNull(serviceName)) {
            ServiceInstance service = localService.getByName(serviceName);
            if (service == null) {
                logger.warn("[NULL-service]serviceName={}", serviceName);
                return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "no service by name " + serviceName));
            }
            sid = service.getId();
        }

        // 3.获取fid
        Integer fid = null;
        if (Objects.nonNull(sid) && Objects.nonNull(method)) { // method有效时，sid也必须有效才行。
            ServiceFunction serviceFunction = localFunction.serviceFunctionByName(sid, method);
            if (Objects.isNull(serviceFunction)) {
                logger.warn("[NULL-serviceFunction]sid={},method={}", sid, method);
                return new ServiceResult<>(new Status(Statuses.NO_SERVICE_FUNCTION, "no serviceFunction by method " + method));
            }

            fid = serviceFunction.getId();
        }

        // 4.从DB获取熔断配置信息
        List<CircuitBreakConfig> circuitBreakConfigPos = circuitBreakConfigDao.queryCircuitBreakConfigs(caller.getId(), sid, fid);

        // 5.结果转换
        Map<String, Map<String, CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta>> serviceMethodConfigMap = new HashMap<>();
        for (CircuitBreakConfig circuitBreakConfigPo : circuitBreakConfigPos) {
            String tmpService = localService.getById(circuitBreakConfigPo.getSid()).getServiceName();
            String tmpMethod = localFunction.function(circuitBreakConfigPo.getFid());

            Map<String, CircuitBreakConfigMeta> method2ConfigMap = serviceMethodConfigMap.get(tmpService);
            if (Objects.isNull(method2ConfigMap)) {
                method2ConfigMap = new HashMap<>();
                serviceMethodConfigMap.put(tmpService, method2ConfigMap);
            }

            method2ConfigMap.put(tmpMethod, transformCircuitBreakConfigMeta(circuitBreakConfigPo));
        }

        return new ServiceResult<>(ServiceResult.SUCCESS, new CallerCircuitBreakConfigMeta(serviceMethodConfigMap));
    }

    private CircuitBreakConfigMeta transformCircuitBreakConfigMeta(CircuitBreakConfig circuitBreakConfigPo) {
        CircuitBreakConfigMeta res = new CircuitBreakConfigMeta();

        res.setEnabled(circuitBreakConfigPo.isEnabled());
        res.setForceOpened(circuitBreakConfigPo.isForceOpened());
        res.setForceClosed(circuitBreakConfigPo.isForceClosed());
        res.setSlideWindowInSeconds(circuitBreakConfigPo.getSlideWindowInSeconds());
        res.setRequestVolumeThreshold(circuitBreakConfigPo.getRequestVolumeThreshold());
        res.setErrorThresholdPercentage(circuitBreakConfigPo.getErrorThresholdPercentage());
        res.setSleepWindowInMilliseconds(circuitBreakConfigPo.getSleepWindowInMilliseconds());

        return res;
    }

    public ServiceResult<ServiceRejectMeta> getServiceRejectMeta(String serviceName, String callerKey) {
        Caller caller = localCaller.getByCallerKey(callerKey);
        if (caller == null) {
            logger.warn("no caller key {}", callerKey);
            return new ServiceResult<>(new Status(Statuses.NO_CALLER, "no caller by callerKey " + callerKey));
        }

        ServiceInstance service = localService.getByName(serviceName);
        if (service == null) {
            logger.warn("no service by service name  {}", service);
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "no service by name " + serviceName));
        }

        CallerUseage callerUseage = callerUsageDao.selectByCidSid(caller.getId(), service.getId());
        List<CallerFunctionUseage> callerFunctionUseages = callerFuncUsageDao.selectByCidAndSid(caller.getId(), service.getId());
        ServiceRejectMeta serviceRejectMeta = new ServiceRejectMeta();
        serviceRejectMeta.setCallerKey(callerKey).setGranularity(callerUseage.getGranularity()).setReject(callerUseage.isReject());
        if (callerFunctionUseages != null) {
            Set<String> notRejectFunctions = callerFunctionUseages.stream().map(CallerFunctionUseage::getFname).collect(Collectors.toSet());
            serviceRejectMeta.setNotRejectFunction(notRejectFunctions);
        }
        return new ServiceResult<>(ServiceResult.SUCCESS, serviceRejectMeta);
    }

    /**
     * 函数名
     */
    private LoadingCache<Integer, Set<String>> serviceId2FunctionsLocalCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<Integer, Set<String>>() {
        @Override
        public Set<String> load(Integer s)
                throws Exception {
            try {
                return serviceFunctionDao.getServiceFunctionsBySid(s).stream().map(ServiceFunction::getFname).collect(Collectors.toSet());
            } catch (Throwable e) {
                return new HashSet<>();
            }

        }
    });

    public ServiceResult<Boolean> persistServiceFunctions(ServiceFunctions request, String remoteIp)
            throws ExecutionException {
        logger.info("op=start_persistServiceFunctions, request={},remoteIp={}", request, remoteIp);
        ServiceInstance service = localService.getByName(request.getServiceName());
        if (Objects.isNull(service)) {
            logger.warn("[warn_persistServiceFunctions_no_service]serviceName={}", request.getServiceName());
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, String.format("服务名为%s的服务不存在！", request.getServiceName())));
        }

        Set<String> dbServiceFunctions = serviceId2FunctionsLocalCache.get(service.getId());

        if (dbServiceFunctions.containsAll(request.getFunctionNames())) {
            return new ServiceResult<>(ServiceResult.SUCCESS, true);
        }
        synchronized (this) {
            if (!dbServiceFunctions.containsAll(request.getFunctionNames())) {
                List<String> newUploadFunctions = new ArrayList<>(Sets.difference(request.getFunctionNames(), dbServiceFunctions));
                logger.info("op=persistServiceFunctions,newUploadFunctions={}", newUploadFunctions);
                try {
                    serviceFunctionDao.addFunctions(service.getId(), newUploadFunctions, remoteIp);
                } catch (DuplicateKeyException e) {
                    logger.warn("[warn_persistServiceFunctions_duplicate_functionName]newUploadFunctions={}", newUploadFunctions);
                }

            }
        }
        return new ServiceResult<>(ServiceResult.SUCCESS, true);
    }

    /**
     *  处理上传的函数签名
     *
     * @param request 上传的请求信息，包括服务名，函数签名等
     * @param remoteIp 上传的客户端ip
     * @return 上传的结果
     * @throws ExecutionException
     */
    public ServiceResult<Boolean> persistServiceFunctionSignature(ServiceFunctions request, String remoteIp)
            throws ExecutionException {
        logger.info("op=start_persistServiceFunctionSignature,request={},remoteIp={}", request, remoteIp);
        ServiceInstance service = localService.getByName(request.getServiceName());
        if (Objects.isNull(service)) {
            logger.warn("warn_persistServiceFunctionSignature_no_service]serviceName={}", request.getServiceName());
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, String.format("服务名为%s的服务不存在！", request.getServiceName())));
        }
        synchronized (this) {
            refreshFunctionSignature(service.getId(), request.getFunctionSignatures(), remoteIp);
            return new ServiceResult<>(ServiceResult.SUCCESS, true);
        }
    }

    /**
     *  存储上传的函数签名，不存在则插入，存在则更新
     *
     * @param sid 服务id
     * @param MethodKeys 函数签名集合
     * @param ip 上传的客户端ip
     */
    private void refreshFunctionSignature(int sid, Set<MethodKey> methodKeys, String ip) {
        int insertTotal = 0, updateTotal = 0;

        for (MethodKey methodKey : methodKeys) {
            String functionName = methodKey.getLookup() + "." + methodKey.getMethodSignatureWithGenericTypes();
            ServiceFunction serviceFunction = serviceFunctionDao.getServiceFunctionsBySidAndFname(sid, functionName);

            if (Objects.isNull(serviceFunction)) {
                int insertCount = serviceFunctionDao.insertFunctionSignature(sid, methodKey, ip);
                insertTotal += insertCount;
                logger.info("op=refreshFunctionSignature,insertCount={},sid={},methodKey={},ip={}", insertCount, sid, methodKey, ip);
            } else {
                int updateCount = serviceFunctionDao.updateFunctionSignature(sid, methodKey, ip);
                updateTotal += updateCount;
                logger.info("op=refreshFunctionSignature,updateCount={},sid={},methodKey={},ip={},serviceFunctions={}", updateCount, sid, methodKey, ip, serviceFunction);
            }
        }
        logger.info("op=end_refreshFunctionSignature,insertTotal={},updateTotal={},sid={},ip={},functionNum={}", insertTotal, updateTotal, sid, ip, methodKeys.size());
    }

    public ServiceResult<Boolean> saveServiceConfig(String serviceName, String config, String log4j2, String ip) {
        ServiceInstance service = localService.getByName(serviceName);
        ServiceConfig serviceConfig = serviceConfigDao.selectServiceConfigBySidAndIp(service.getId(), ip);
        if (Objects.isNull(serviceConfig)) {
            serviceConfig = new ServiceConfig(service.getId(), ip, config, log4j2);
            serviceConfigDao.insertServiceConfig(serviceConfig);
        } else {
            serviceConfigDao.updateServiceConfigBySidAndIp(service.getId(), ip, config, log4j2);
        }
        return new ServiceResult<>(ServiceResult.SUCCESS, true);
    }

    public ServiceResult<Boolean> saveClientConfig(String callerKey, String ip, String usageConfigs) {
        // 存储数据
        Caller caller = localCaller.getByCallerKey(callerKey);
        ClientConfig clientConfig = clientConfigDao.selectClientConfigByCidAndIp(caller.getId(), ip);
        if (Objects.isNull(clientConfig)) {
            clientConfig = new ClientConfig(caller.getId(), ip, usageConfigs);
            clientConfigDao.insertClientConfig(clientConfig);
        } else {
            clientConfigDao.updateClientConfigByCidAndIp(caller.getId(), ip, usageConfigs);
        }
        return new ServiceResult<>(ServiceResult.SUCCESS, true);
    }

    public ServiceResult<ServiceInstance> getServiceByServiceName(String serviceName) {
        ServiceInstance res = serviceDao.selectByName(serviceName);
        if (Objects.isNull(res)) {
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "服务不存在"), null);
        }
        return new ServiceResult<>(ServiceResult.SUCCESS, res);

    }

    public ServiceResult<String> saveServiceExt(String serviceName, String ext, String ip) {
        ServiceInstance service = localService.getByName(serviceName);
        if (Objects.isNull(service)) {
            return new ServiceResult<>(new Status(Statuses.NO_SERVICE, "服务不存在" + serviceName));
        }

        ServiceConfig existServiceConfig = serviceConfigDao.selectServiceConfigBySidAndIp(service.getId(), ip);
        int count;
        if (Objects.isNull(existServiceConfig)) {
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setSid(service.getId());
            serviceConfig.setIp(ip);
            serviceConfig.setExt(ext);
            count = serviceConfigDao.insertServiceConfig(serviceConfig);
        } else {
            count = serviceConfigDao.updateExtBySidAndIp(service.getId(), ip, ext);
        }
        if (count != 1) {
            return new ServiceResult<>(ServiceResult.SERVER_ERROR);
        }
        return new ServiceResult<>(ServiceResult.SUCCESS);
    }
}
