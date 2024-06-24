package com.bin.webmonitor.controller.op;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.dao.ServiceNodeGroupDao;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.repository.domain.ServiceNodeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("opServiceNodeController")
@RequestMapping("/op/service/node")
public class ServiceNodeController extends BaseController {
    
    @Autowired
    private ServiceDao serviceDao;
    
    @Autowired
    private ServiceNodeGroupDao serviceNodeGroupDao;
    
    @Autowired
    private NamingProxy namingProxy;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * 获取分组中无效的云节点
     *
     * @return
     */
    @RequestMapping(value = "getInvalidGroupCloudNodes", method = RequestMethod.GET)
    public ApiResult<Map<String, Map<String, ServiceNode>>> getInvalidGroupCloudNodes()
        throws ParseException {
        Map<String, Map<String, ServiceNode>> res = new HashMap<>();
        
        List<ServiceInstance> services = serviceDao.selectAll();
        for (ServiceInstance service : services) {
         // 获取该服务的所有分组节点
            List<ServiceNodeGroup> dbNodes = serviceNodeGroupDao.selectBySid(service.getId());
            if (CollectionUtils.isEmpty(dbNodes)) {
                continue;
            }
            Map<String, ServiceNode> ip2InvalidCloudNodeMap = new HashMap<>();
            
            // 从控制中心获取最新节点状态
            List<ServiceNode> serviceNodes = namingProxy.getNodes(service.getServiceName()).checkAndReturn();
            Map<String, ServiceNode> ip2ServiceNodeMap = serviceNodes.stream().collect(Collectors.toMap(ServiceNode::getIp, Function.identity()));
            
            for (ServiceNodeGroup dbNode : dbNodes) {
                // 非云节点
                if (!(StringUtil.startsWith(dbNode.getIp(), "10.151") || StringUtil.startsWith(dbNode.getIp(), "10.165"))) {
                    continue;
                }
                
                ServiceNode serviceNode = ip2ServiceNodeMap.get(dbNode.getIp());
                if (Objects.isNull(serviceNode)) {
                    ip2InvalidCloudNodeMap.put(dbNode.getIp(), null);
                    continue;
                }
                
                if (!serviceNode.isRunning() || !serviceNode.isInService()) {
                    // 借用callerKey字段
                    serviceNode.setCallerKey("状态异常");
                    ip2InvalidCloudNodeMap.put(dbNode.getIp(), serviceNode);
                    continue;
                }

                Date updateDate = TimeUtil.allTimeStr2Date("" + serviceNode.getUpdateTime());
                if (System.currentTimeMillis() - updateDate.getTime() > 60000) {
                    // 借用callerKey字段
                    serviceNode.setCallerKey("更新时间异常");
                    ip2InvalidCloudNodeMap.put(dbNode.getIp(), serviceNode);
                }
            }
            
            if (null != ip2InvalidCloudNodeMap && ip2InvalidCloudNodeMap.size() > 0) {
                res.put(service.getServiceName(), ip2InvalidCloudNodeMap);
            }
        }
        
        logger.info("op=end_getInvalidGroupCloudNodes,res={}",  res);
        return okJson(res);
    }
}
