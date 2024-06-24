package com.bin.webmonitor.component;

import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ServiceValidator {


    private static final String REGEX_SERVICE_NAME = "[A-Za-z]+[A-Za-z0-9_]{0,99}";

    @Autowired
    private ServiceDao serviceDao;

    /**
     * 校验 服务名字
     *
     * @param serviceName
     * @return
     */
    public ApiResult<Boolean> validateServiceName(String serviceName) {
        if (StringUtil.isEmpty(serviceName)) {
            return new ApiResult<Boolean>().setCode(1).setMessage("服务名不能为空！");
        }

        if (serviceName.length() >= 100) {
            return new ApiResult<Boolean>().setCode(1).setMessage("服务名长度不能大于100！").setField(serviceName);
        }

        if (!serviceName.matches(REGEX_SERVICE_NAME)) {
            return new ApiResult<Boolean>().setCode(1).setMessage("服务名只能是字母、数字和下划线，且首字符必须是字母！").setField(serviceName);
        }
        return null;
    }



    /**
     * 校验描述decription
     *
     * @param description
     * @return
     */
    private ApiResult<Boolean> validateDescription(String description) {
        if (StringUtil.isEmpty(description)) {
            return new ApiResult<Boolean>().setCode(1).setMessage("服务描述不能为空！");
        }
        if (description.length() > 500) {
            return new ApiResult<Boolean>().setCode(1).setMessage("服务描述长度不能大于500");
        }
        return null;
    }


    /**
     * 校验tcp端口
     *
     * @param tcpPort
     * @return
     */
    private ApiResult<Boolean> validateTcpPort(int tcpPort, List<ServiceInstance> services) {
        if (tcpPort > 65535 || tcpPort <= 0) {
            return new ApiResult<Boolean>().setCode(1).setMessage("tcpPort取值范围【1-65535】");
        }
        Set<Integer> tcpPorts = services.stream().map(ServiceInstance::getTcpPort).collect(Collectors.toSet());
        if (tcpPorts.contains(tcpPort)) {
            return new ApiResult<Boolean>().setCode(1).setMessage("tcp端口已存在！请使用其他端口！");
        }
        return null;
    }

    public ApiResult<Boolean> validateService(ServiceInstance service) {
        ApiResult<Boolean> result;

        List<ServiceInstance> existServices = serviceDao.selectAll().stream()
                .filter(existService -> existService.getId() != service.getId()).collect(Collectors.toList());

        result = validateServiceName(service.getServiceName());
        if (result != null) {
            return result;
        }



        result = validateDescription(service.getDescription());
        if (result != null) {
            return result;
        }

        result = validateTcpPort(service.getTcpPort(), existServices);
        return result;
    }


}

