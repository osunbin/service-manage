package com.bin.client.router.matcher.pattern;


import com.bin.client.router.RouterContext;
import com.bin.webmonitor.common.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Matches with patterns like 'key=1~100', 'key=~100' or 'key=1~'
 */
public class RangeValuePattern implements ValuePattern{
    private  final Logger logger = LogManager.getLogger(RangeValuePattern.class);

    @Override
    public boolean shouldMatch(String pattern) {
        return pattern.contains("~");
    }

    @Override
    public boolean match(String pattern, String value, RouterContext context, boolean isWhenCondition) {
        boolean defaultValue = !isWhenCondition;
        try {
            int intValue = Integer.parseInt(value);

            String[] arr = pattern.split("~");
            if (arr.length < 2) {
                logger.error(
                        "Invalid condition rule " + pattern + " or value " + value + ", will ignore.");
                return defaultValue;
            }

            String rawStart = arr[0];
            String rawEnd = arr[1];

            if (StringUtil.isEmpty(rawStart) && StringUtil.isEmpty(rawEnd)) {
                return defaultValue;
            }

            if (StringUtil.isEmpty(rawStart)) {
                int end = Integer.parseInt(rawEnd);
                if (intValue > end) {
                    return false;
                }
            } else if (StringUtil.isEmpty(rawEnd)) {
                int start = Integer.parseInt(rawStart);
                if (intValue < start) {
                    return false;
                }
            } else {
                int start = Integer.parseInt(rawStart);
                int end = Integer.parseInt(rawEnd);
                if (intValue < start || intValue > end) {
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(
                    "Parse integer error Invalid condition rule " + pattern + " or value " + value + ", will ignore.",
                    e);
            return defaultValue;
        }

        return true;
    }
}
