package com.bin.webmonitor.command;

import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Commands {
    private static Logger logger = LoggerFactory.getLogger(BaseCommand.class);
    
    /**
     * 降级指令
     */
    public static final int DEGRADE = 1;
    
    /**
     * 限流指令
     */
    public static final int LIMIT = 2;
    
    /**
     * 拒绝指令
     */
    public static final int REJECT = 3;
    
    /**
     * 服务更新指令：服务提供方节点变更
     */
    public static final int SERVICE_COUNTER = 4;
    
    /**
     * 配置更新指令
     */
    public static final int CONFIG_CHANGE = 6;
    
    /**
     * 时间更新指令：统计 or 收集单位变更
     */
    public static final int BASE_TIME_CHANGE = 7;
    
    /**
     * 熔断配置指令
     * TODO: 严格起来属于配置更新指令一部分，没设计好。可优化合并
     */
    public static final int CIRCUIT_BREAK_CONFIG_CHANGE = 9;
    
    /**
     * @方法名称 parse
     * @功能描述 解析报文获取指令对象
     * @param content 报文
     * @return 指令对象
     */
    public static BaseCommand parse(String content) {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        try {
            int code = Integer.parseInt(content.substring(0, 1));
            String con = content.substring(1);
            if (code == DEGRADE) {
                return JsonHelper.fromJson(con, DegradeCommand.class);
            } else if (code == LIMIT) {
                return JsonHelper.fromJson(con, LimitCommand.class);
            } else if (code == REJECT) {
                return JsonHelper.fromJson(con, RejectCommand.class);
            } else if (code == SERVICE_COUNTER) {
                return JsonHelper.fromJson(con, ServiceCounterFreshCommand.class);
            } else if (code == CONFIG_CHANGE) {
                return JsonHelper.fromJson(con, ConfigChangeCommand.class);
            } else if (code == BASE_TIME_CHANGE) {
                return JsonHelper.fromJson(con, BaseTimeChangeCommond.class);
            } else if (code == CIRCUIT_BREAK_CONFIG_CHANGE) {
                return JsonHelper.fromJson(con, CircuitBreakConfigChangeCommand.class);
            } else {
                return null;
            }
        } catch (Throwable e) {
            logger.error("[ARCH_SDK_error_parse_command]content={}", content, e);
            return null;
        }
    }
}
