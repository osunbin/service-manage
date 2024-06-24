package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.model.LabelValue;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/common")
public class CommonController {

   

    @Autowired
    private CallerDao callerDao;

    @Autowired
    private ServiceDao serviceDao;


    @RequestMapping("/allCallers")
    public ApiResult<List<LabelValue>> allCallers() {
        List<Caller> callers = callerDao.selectAll();
        List<LabelValue> callerLables = new ArrayList<>();
        if (callers != null)  {
            callerLables = callers.stream().map(c -> new LabelValue().setLabel(c.getCallerName()).setValue(String.valueOf(c.getId()))).collect(Collectors.toList());
        }
        return new ApiResult<List<LabelValue>>().setCode(Constants.SUCCESS).setResult(callerLables);
    }

    @RequestMapping("/allService")
    public ApiResult<List<LabelValue>> allService() {
        List<ServiceInstance> services = serviceDao.selectAll();
        List<LabelValue> callerLables;
        if (services == null) {
            callerLables = new ArrayList<>();
        } else {
            callerLables = services.stream().map(c -> new LabelValue().setLabel(c.getServiceName()).setValue(String.valueOf(c.getId()))).collect(Collectors.toList());
        }
        return new ApiResult<List<LabelValue>>().setCode(Constants.SUCCESS).setResult(callerLables);
    }


}
