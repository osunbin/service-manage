package com.bin.client;

import com.bin.client.circuitbreak.CircuitBreakConfigChangeListener;
import com.bin.webmonitor.command.BaseCommand;
import com.bin.webmonitor.command.CircuitBreakConfigChangeCommand;
import com.bin.webmonitor.command.Commands;
import com.bin.webmonitor.command.ConfigChangeCommand;
import com.bin.webmonitor.command.DegradeCommand;
import com.bin.webmonitor.command.LimitCommand;
import com.bin.webmonitor.command.RejectCommand;
import com.bin.webmonitor.command.ServiceCounterFreshCommand;
import com.bin.webmonitor.naming.GiveCommand;
import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CommandListener {
    private static Logger logger = LoggerFactory.getLogger(CommandListener.class);


    private static CommandListener instance = new CommandListener();



    public static CommandListener getInstance() {
        return instance;
    }


    public void listen(GiveCommand giveCommand) {
        if (giveCommand == null) {
            return;
        }
        try {
            // 1、获取指令对象
            BaseCommand com = Commands.parse(giveCommand.getContent());
            if (com == null) {
                return;
            }
            logger.info("[ARCH_SDK_reveive_command]giveCommand={},com={}", giveCommand, com);
            // 2、执行指令
            if (com instanceof DegradeCommand) {
                ClientService.instance.refreshDegradeConfig(((DegradeCommand)com).getServiceName());
            } else if (com instanceof LimitCommand) {
                ServerService.instance.limit(((LimitCommand)com).getCallerKey(), ((LimitCommand)com).getFunction(), ((LimitCommand)com).getTime());
            } else if (com instanceof RejectCommand) {
                ServerService.instance.refreshCallerMethod(((RejectCommand)com).getCallerKey());
            } else if (com instanceof ServiceCounterFreshCommand) {
                ServerService.instance.refreshCallerkeyIdMap();
            } else if (com instanceof ConfigChangeCommand) {
                ClientService.instance.refreshConfig(((ConfigChangeCommand)com).getService());
            }  else if (com instanceof CircuitBreakConfigChangeCommand) {
                // 2.1 熔断配置变更指令
                CircuitBreakConfigChangeCommand circuitBreakConfigChangeCommand = (CircuitBreakConfigChangeCommand)com;
                // 刷新配置
                ClientService.instance.refreshCircuitBreakConfig(circuitBreakConfigChangeCommand.getService(), circuitBreakConfigChangeCommand.getMethod());
                // 通知listener
                CircuitBreakConfigChangeListener circuitBreakConfigChangeListener = ClientService.instance.getCircuitBreakConfigChangeListener();
                if (Objects.nonNull(circuitBreakConfigChangeListener)) {
                    CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta circuitBreakConfigMeta = ClientService.instance.getCircuitBreakConfig(circuitBreakConfigChangeCommand.getService(), circuitBreakConfigChangeCommand.getMethod());
                    circuitBreakConfigChangeListener.onConfigChange(circuitBreakConfigChangeCommand.getService(), circuitBreakConfigChangeCommand.getMethod(), circuitBreakConfigMeta);
                }
            }
        } catch (Throwable e) {
            logger.error("[ARCH_SDK_error_listen_command]giveCommand={}", giveCommand, e);
        }

    }
}
