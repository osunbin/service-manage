package com.bin.webmonitor.component;

import com.bin.webmonitor.command.BaseCommand;
import com.bin.webmonitor.command.CircuitBreakConfigChangeCommand;
import com.bin.webmonitor.command.ConfigChangeCommand;
import com.bin.webmonitor.command.DegradeCommand;
import com.bin.webmonitor.naming.GiveCommand;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.model.Response;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalFunction;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SendCommand {

    private  final Logger logger = LoggerFactory.getLogger(SendCommand.class);


    @Autowired
    private LocalCaller localCaller;
    @Autowired
    private LocalService localService;
    @Autowired
    private LocalFunction localFunction;


    @Autowired
    private NamingProxy namingProxy;


    public void sendClientConfigChange(Caller caller, ServiceInstance service) {
        sendClient(caller.getCallerKey(), new ConfigChangeCommand().setService(service.getServiceName()));
    }


    public void sendService(String service, BaseCommand command) {
        try {
            GiveCommand com = new GiveCommand();
            com.setServiceName(service);
            com.setServerMode(true);
            com.setContent(command.toContent());
            Response<?> re = namingProxy.execute(com);
            logger.info("send service {} command {} com {} re {}", service, command, com, re);
        } catch (Throwable e) {
            logger.error("send service {} command {} error ", service, command, e);
        }
    }

    /**
     *
     * @param callerKey  qz9ZoZWTD2I0drDK+qbKCQ==
     * @param command
     */
    public void sendClient(String callerKey, BaseCommand command) {
        try {
            GiveCommand com = new GiveCommand();
            com.setCallerKey(callerKey);
            com.setServerMode(false);
            com.setContent(command.toContent());
            Response<?> re = namingProxy.execute(com);
            logger.info("send caller {} comand {} re {}", callerKey, command, re);
        } catch (Throwable e) {
            logger.info("send caller {} comand {} error", callerKey, command, e);
        }
    }

    public void sendDegradeCommand(int sid, int cid) {
        Caller caller = localCaller.getById(cid);
        if (caller != null) {
            ServiceInstance service = localService.getById(sid);
            if (service != null) {
                sendClient(caller.getCallerKey(), new DegradeCommand().setServiceName(service.getServiceName()));
            }
        }
    }

    /**
     * 发送熔断配置变更命令
     * 
     * @param cid
     * @param sid
     * @param fid
     */
    public void sendCircuitBreakConfigChangeCommand(int cid, int sid, int fid) {
        Caller caller = localCaller.getById(cid);
        ServiceInstance service = localService.getById(sid);
        String method = localFunction.function(fid);
        if (Objects.isNull(caller) || Objects.isNull(service) || Objects.isNull(method)) {
            logger.error(
                "[ERROR-sendCircuitBreakConfigChangeCommand-NullPoint]cid={},sid={},fid={},caller,service,method", cid,
                sid, fid, caller, service, method);
        }

        sendClient(caller.getCallerKey(),
            new CircuitBreakConfigChangeCommand(service.getServiceName(), method));

        logger.info("op=end_sendCircuitBreakConfigChangeCommand,cid={},sid={},fid={}", cid, sid, fid);
    }
}
