package com.bin.webmonitor.controller.op;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/op/serviceTran")
@RestController
public class ServiceTranController extends BaseController {
    @Autowired
    private CoreService coreService;

    
    @Autowired
    private NamingProxy namingProxy;
    
    ExecutorService es = Executors.newFixedThreadPool(15);
    

    
    @RequestMapping("/serviceList")
    @SuppressWarnings("rawtypes")
    public DataGrid<ServiceInstance> serviceList( @PageableDefault Pageable pageable, ServiceInstance service)
        throws Exception {
        
        List<ServiceInstance> services = coreService.serviceListForTran(pageable, service);
        int total = coreService.countServiceListForTran(service);
        
        // 获取异常量和拒绝量
        Calendar now = Calendar.getInstance();
        long end = now.getTimeInMillis();
        long start = end - TimeUnit.MINUTES.toMillis(20);
        List<Future> tasks = new ArrayList<>();
        for (ServiceInstance se : services) {
            tasks.add(es.submit(() -> {
                // 判断servicenode中是否有正在运行
                List<ServiceNode> nodes = namingProxy.getNodes(se.getServiceName()).checkAndReturn();
                if (CollectionUtils.isEmpty(nodes)) {
                    se.setDescription("无节点");
                }
                for (ServiceNode node : nodes) {
                    if (node.isRunning() && node.isInService()) {
                        se.setDescription(node.getContainerVersion() + "-正在运行");
                        break;
                    } else {
                        se.setDescription(node.getContainerVersion());
                    }
                }
            }));
            
        }
        for (Future future : tasks) {
            future.get();
        }
        
        Map<String, Object> searchMap = new HashMap<>(3);
        if (!StringUtil.isEmpty(service.getServiceName())) {
            searchMap.put("serviceName", service.getServiceName());
        }

        DataGrid<ServiceInstance> dataGrid = new DataGrid<>(pageable.getPageNumber(), pageable.getPageSize(), total, services, searchMap);
        return dataGrid;
    }
    

}
