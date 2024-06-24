package com.bin.webmonitor.externapi;

import com.bin.webmonitor.component.NodeOnlineOffline;
import com.bin.webmonitor.naming.ServiceNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/naming")
public class NamingApi {


    @Autowired
    private NodeOnlineOffline nodeOnlineOffline;


    @RequestMapping("/online")
    public void online(ServiceNode serviceNode) {
        nodeOnlineOffline.processMessage(serviceNode,true);
    }

    @RequestMapping("/offline")
    public void offline(ServiceNode serviceNode) {
        nodeOnlineOffline.processMessage(serviceNode,false);
    }


}
