package com.bin.webmonitor.component;

import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.naming.ServiceNodes;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.dao.ServiceNodeGroupDao;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.repository.domain.ServiceNodeGroup;
import com.bin.webmonitor.service.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class NodeOnlineOffline {
    private static Logger logger = LoggerFactory.getLogger(NodeOnlineOffline.class);
    
    @Autowired
    private ServiceDao serviceDao;
    
    @Autowired
    private ServiceNodeGroupDao serviceNodeGroupDao;
    
    @Autowired
    private CoreService coreService;

    @Autowired
    private ServiceNodes serviceNodes;

    @Value("filterIp:1")
    private String filterIp;

    public void processMessage(ServiceNode serviceNode, boolean isOnline) {

        logger.info("op=start_doProcessMessage,serviceNode={},isOnline={}", serviceNode, isOnline);
        Set<String> filterIps = new HashSet<>(DelimiterHelper.splitToList(filterIp));
        String targetIp = serviceNode.getIp();
        // 节点过滤，测试使用
        if (!CollectionUtil.isEmpty(filterIps) && filterIps.contains(targetIp)) {
            logger.info("[IGNORE_cloudNode]targetIp={},ips={},serviceNode={},isOnline={}", targetIp, filterIps, serviceNode, isOnline);
            return;
        }

        if (!serviceNodes.isDocker(serviceNode.getServerType())) {
            logger.info("[NOT_cloud_node]serviceNode={}", serviceNode);
            return;
        }

        if (!(serviceNodes.isOnline(serviceNode.getSystemEnvType()) || serviceNodes.isGray(serviceNode.getSystemEnvType()))) {
            // 非线上结点和灰度节点，不处理
            logger.info("[cloud_node_env_not_online]serviceNode={}", serviceNode);
            return;
        }
        ServiceInstance service = serviceDao.selectByName(serviceNode.getServiceName());
        if (service == null) {
            logger.warn("[null_service]serviceNode={}", serviceNode);
            return;
        }
        
        int gid = getGid(serviceNode);
        if (gid != 0) {
            // 将云结点加入分组中
            addOrDeleteCloudNode2Group(serviceNode, gid, isOnline);
        }
        // 向该服务的所有调用方发送配置变更指令
        coreService.sendConfigChange(service, gid == 0 ? null : gid);
        logger.info("op=end_doProcessMessage,serviceNode={},isOnline={}", serviceNode, isOnline);
    }
    
    /**
     * 从分组中加入或删除云结点
     * 
     * @param serviceNode
     * @param gid
     * @param isOnline
     */
    private void addOrDeleteCloudNode2Group(ServiceNode serviceNode, int gid, boolean isOnline) {
        List<ServiceNodeGroup> beforeGroupNodes = serviceNodeGroupDao.selectByGid(gid);
        
        ServiceNodeGroup serviceNodeGroup = new ServiceNodeGroup().setGid(gid).setIp(serviceNode.getIp());
        if (isOnline) {
            serviceNodeGroupDao.batchInsert(Collections.singletonList(serviceNodeGroup));
        } else {
            serviceNodeGroupDao.deleteBatch(Collections.singletonList(serviceNodeGroup));
        }
        
        List<ServiceNodeGroup> afterGroupNodes = serviceNodeGroupDao.selectByGid(gid);
        logger.info("op=end_addOrDeleteCloudNode2Group,serviceNode={},gid={},isOnline={},beforeGroupNodes={},afterGroupNodes={}", serviceNode, gid, isOnline, beforeGroupNodes, afterGroupNodes);
    }
    
    private String generateTaskCoordinaterKey(String serviceName, String ip, boolean isOnline) {
        String format = isOnline ? "server-docker-online:%s:%s" : "server-docker-offline:%s:%s";
        return String.format(format, serviceName, ip);
    }
    
    private int getGid(ServiceNode serviceNode) {
        if (!StringUtil.isCreatable(serviceNode.getGroupArray())) {
            logger.warn("[cloudNode_illegal_gid]serviceNode={}", serviceNode);
            return 0;
        }
        
        return Integer.parseInt(serviceNode.getGroupArray());
    }
}
