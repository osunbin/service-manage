package com.bin.webmonitor.common;

import com.bin.webmonitor.common.util.StringUtil;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.Set;

public class Constants {

    /**
     * 服务管理平台地址
     */
    public static final String SERVER_MANAGE_URL = "http://localhost:28806";


    /**
     * 调用关系粒度（函数级）
     */
    public static final int USAGE_GRANULARITY_FUNCTION = 0;

    /**
     * 调用关系粒度（服务级）
     */
    public static final int USAGE_GRANULARITY_SERVER = 1;

    /**
     * 输入校验有问题
     */
    public static final int NOT_SUCCESS = 1;
    /**
     * 业务逻辑处理出现异常
     */
    public static final int SERVER_ERROR = -1;

    public static final int SUCCESS = 0;

    public static final int ZERO = 0;

    public static final int ONE = 1;

    public static final PageRequest PAGE_MAX = PageRequest.of(ZERO, Integer.MAX_VALUE);

    public static final PageRequest PAGE_ONE = PageRequest.of(ZERO, ONE);

    public static final int NOW_YEAR = 2019;


    public static final String DEFAULT_GROUP_NAME = "默认组";

    public static final int SMALL_INT_MAX = 1024 * 1024 * 8;//8M


    /**
     * 一天的毫秒数
     */
    public static final long MILLISECOND_OF_ONE_DAY = 24 * 60 * 60 * 1000;

    public static final String EMPTY_STRING = "";

    public static final String UNKNOWN = "unknown";

    public static final String SEPARATOR_UNDERLINE = "_";

    public static final String FLUX_LIMIT_TAG = "fluxLimit";

}
