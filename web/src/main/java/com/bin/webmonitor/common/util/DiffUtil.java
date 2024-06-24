package com.bin.webmonitor.common.util;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.model.dto.FuncConfigDto;
import com.bin.webmonitor.model.dto.FunctionTimeoutDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DiffUtil {

    private static Logger logger = LoggerFactory.getLogger(DiffUtil.class);


    public static <T> String diffObjectField(T before, T after, Set<String> ignoreFieldName) {
        if (Objects.isNull(before) && Objects.isNull(after)) {
            return "";
        } else if (Objects.isNull(before)) {
            return "该对象由null变为" + after;
        } else if (Objects.isNull(after)) {
            return "该对象由" + before + "变为null";
        }
        StringBuilder diff = new StringBuilder();

        Field[] fields = before.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                if (ignoreFieldName.contains(field.getName())) {
                    continue;
                }
                field.setAccessible(true);
                String beforeVal = String.valueOf(field.get(before));
                String afterVal = String.valueOf(field.get(after));
                if (!Objects.equals(beforeVal, afterVal)) {
                    diff.append("字段：").append(field.getName()).append("，旧值：").append(beforeVal).append("，新值：").append(afterVal).append("；");
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("ERROR_diffCallerUsage,before={},after={}", before, after, e);
        }
        return diff.toString();
    }

    public static String diffFunctionQuantity(String functionConfigJson) {
        if (StringUtil.isEmpty(functionConfigJson)) {
            return Constants.EMPTY_STRING;
        }
        List<FuncConfigDto> funcConfigDtos = JsonHelper.strToList(functionConfigJson, FuncConfigDto.class);
        StringBuilder builder = new StringBuilder();
        for (FuncConfigDto funcConfigDto : funcConfigDtos) {
            if (funcConfigDto.getDelta() == 0) {
                continue;
            }
            builder.append(funcConfigDto.getFuncName()).append(":");
            builder.append("旧值：").append(Integer.parseInt(funcConfigDto.getQuantity()) - funcConfigDto.getDelta()).append("次，");
            builder.append("新值：").append(Integer.parseInt(funcConfigDto.getQuantity())).append("次；");
        }
        return builder.toString();
    }

    public static String diffFunctionTimeout(String functionTimeoutJson) {
        if (StringUtil.isEmpty(functionTimeoutJson)) {
            return Constants.EMPTY_STRING;
        }
        List<FunctionTimeoutDto> functionTimeoutDtos =  JsonHelper.strToList(functionTimeoutJson, FunctionTimeoutDto.class);
        if(CollectionUtil.isEmpty(functionTimeoutDtos)){
            return Constants.EMPTY_STRING;
        }

        StringBuilder builder = new StringBuilder();
        for (FunctionTimeoutDto functionTimeoutDto : functionTimeoutDtos) {

            int originFunctionTimeout = functionTimeoutDto.getOriginFunctionTimeout();
            int newFunctionTimeout = functionTimeoutDto.getFunctionTimout();
            if (originFunctionTimeout == newFunctionTimeout) {
                continue;
            }
            builder.append(functionTimeoutDto.getFunctionName()).append(":");
            builder.append("旧值：").append(originFunctionTimeout).append("ms,");
            builder.append("新值：").append(newFunctionTimeout).append("ms;");
        }
        if (builder.length() > 0) {
            builder.insert(0,"函数超时：").setCharAt(builder.length() - 1, '.');
        }
        return builder.toString();
    }
}
