package com.bin.webmonitor.service;

import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.model.vo.ServiceNodeVO;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.dao.ClientConfigDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CallerNodeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LocalCaller localCaller;
    @Autowired
    private NamingProxy namingProxy;
    @Autowired
    private ClientConfigDao clientConfigDao;



    public DataGrid<ServiceNodeVO> getNodes(int cid) {
        DataGrid<ServiceNodeVO> serviceNodeVoDataGrid = new DataGrid<>();

        Caller caller = localCaller.getById(cid);
        List<ServiceNode> nodes = namingProxy.getCallerNodes(caller.getCallerKey()).checkAndReturn();
        if (CollectionUtil.isEmpty(nodes)) {
            logger.warn("[WARN_empty_caller_nodes]cid={},caller={},nodes={}", cid, caller, nodes);
            return serviceNodeVoDataGrid;
        }

        List<ServiceNodeVO> serviceNodeVos = nodes.stream().map(node -> {
            ServiceNodeVO serviceNodeVO = new ServiceNodeVO().from(node);
            serviceNodeVO.setIdcName(StringUtil.isEmpty(node.getIdcName()) ? "" : node.getIdcName());
            return serviceNodeVO;
        }).sorted().collect(Collectors.toList());

        serviceNodeVoDataGrid.setRows(serviceNodeVos);
        serviceNodeVoDataGrid.setTotal(serviceNodeVos.size());
        serviceNodeVoDataGrid.setCurrent(1);
        return serviceNodeVoDataGrid;
    }


    public ClientConfig getClientConfig(int cid, String ip) {
        ClientConfig clientConfig = clientConfigDao.selectClientConfigByCidAndIp(cid, ip);
        if (Objects.isNull(clientConfig)) {
            logger.info("op=getClientUsageConfig,config of cid:{} and ip:{} do not exists;", cid, ip);
            return new ClientConfig(cid, ip, "配置信息不存在。\r\n可能因为绑定服务服务平台host问题，不能有效上传配置信息。");
        }

        clientConfig.setUpdateTimeStr(TimeUtil.date2fullStr(clientConfig.getUpdateTime()));
        return clientConfig;
    }
}
