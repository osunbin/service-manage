package com.bin.webmonitor.controller.op;


import java.util.Objects;

import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.CoreService;
import com.bin.webmonitor.component.SendCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/op/command")
public class CommandController extends BaseController {
    @Autowired
    private LocalService localService;
    @Autowired
    private CallerDao callerDao;

    @Autowired
    private SendCommand sendCommand;

    @Autowired
    private CoreService coreService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "sendConfigChange", method = RequestMethod.GET)
    public ApiResult<String> get(@RequestParam("serviceName") String serviceName,
                                 @RequestParam(value = "gid", required = false) Integer gid) {
        ServiceInstance service = localService.getByName(serviceName);
        if (Objects.isNull(service)) {
            return errorJson(String.format("服务名[%s]不存在！！！", serviceName));
        }

        coreService.sendConfigChange(service, gid);

        logger.info("op=end_get,serviceName={},gid={}", serviceName, gid);
        return okJson();
    }


    @RequestMapping(value = "sendClientConfigChange", method = RequestMethod.GET)
    public ApiResult<String> sendClientConfigChange(String callerName, String serviceName) {
        ServiceInstance service = localService.getByName(serviceName);
        if (Objects.isNull(service)) {
            return errorJson(String.format("服务名[%s]不存在！！！", serviceName));
        }
        Caller caller = callerDao.selectByName(callerName);
        if (Objects.isNull(caller)) {
            return errorJson(String.format("调用方[%s]不存在！！！", callerName));
        }
        sendCommand.sendClientConfigChange(caller, service);
        return okJson();
    }

}
