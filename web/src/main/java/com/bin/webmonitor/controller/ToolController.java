package com.bin.webmonitor.controller;

import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RequestMapping("/tool")
@RestController
public class ToolController {



    @Autowired
    private LocalCaller localCaller;

    @Autowired
    private LocalService localService;
    @Autowired
    private CallerUsageDao callerUsageDao;


    @RequestMapping("getConfig")
    public String getConfig(String callerKey, String serviceName) {

        Caller caller = localCaller.getByCallerKey(callerKey);
        ServiceInstance service = localService.getByName(serviceName);

        if (caller == null) {
            return "no caller ";
        }

        if (service == null) {
            return "no service";
        }

        CallerUseage ca = callerUsageDao.selectByCidSid(caller.getId(), service.getId());
        if(ca == null) {
            return caller.toString() + "===========" + service.toString() + "============" + "no callerUsage" ;
        }else {
            return caller.toString() + "===========" + service.toString() + "============" +  ca.toString();
        }
    }

}
