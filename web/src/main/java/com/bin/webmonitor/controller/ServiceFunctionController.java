package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.component.ServiceFunctionService;
import com.bin.webmonitor.model.vo.IdNameVo;
import com.bin.webmonitor.model.vo.ServiceFunctionCountVo;
import com.bin.webmonitor.model.vo.ServiceFunctionGroupVO;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/service/function")
public class ServiceFunctionController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ServiceFunctionController.class);


    @Autowired
    private ServiceFunctionDao serviceFunctionDao;
    @Autowired
    private ServiceFunctionService serviceFunctionService;
    @Autowired
    private LocalService localService;

    /**
     * 全部服务函数.
     */
    @RequestMapping("/names/{sid}")
    public ApiResult<List<IdNameVo>> names(@PathVariable("sid") Integer sid) {
        List<ServiceFunction> svcFuncs = serviceFunctionDao.getServiceFunctionsBySid(sid);
        List<IdNameVo> collect = svcFuncs.stream()
                .map(svcFunc -> new IdNameVo(svcFunc.getId(), svcFunc.getFname()))
                .collect(Collectors.toList());
        return okJson(collect);
    }

    /**
     * 全部服务函数
     *
     * @param cid
     * @param sid
     * @return
     * @throws Exception
     */
    @RequestMapping("/names")
    public ApiResult<List<ServiceFunctionCountVo>> names(@RequestParam("cid") Integer cid,
                                                         @RequestParam("sid") Integer sid) throws Exception {
        logger.info("op=start_names,cid={},sid={}", cid, sid);


        List<ServiceFunction> serviceFunctions = serviceFunctionDao.getServiceFunctionsBySid(sid);
        List<ServiceFunctionCountVo> serviceFunctionCountVos = serviceFunctions.stream().map(
                        serviceFunction -> new ServiceFunctionCountVo(serviceFunction.getId(), serviceFunction.getFname(), 0L))
                .collect(Collectors.toList());

        return okJson(serviceFunctionCountVos);
    }

    @RequestMapping("/list")
    public ApiResult<Map<String,Object>> list(String serviceName, int sid) {
        List<ServiceFunctionGroupVO> serviceFunctionGroupVos = serviceFunctionService.list(sid);

        Map<String,Object> modelMap = new HashMap<>();
        for (ServiceFunctionGroupVO serviceFunctionGroupVO : serviceFunctionGroupVos) {
            serviceFunctionGroupVO.setUpdate(true);
        }
        modelMap.put("service", localService.getById(sid));
        modelMap.put("serviceFunctionGroupVOs", serviceFunctionGroupVos);


        List<ServiceFunction> defaultFunctions = new ArrayList<>();
        if (!CollectionUtil.isEmpty(serviceFunctionGroupVos)) {
            for (ServiceFunctionGroupVO serviceFunctionGroupVO : serviceFunctionGroupVos) {
                if (serviceFunctionGroupVO.getGid() == -1) {
                    defaultFunctions = serviceFunctionGroupVO.getFunctions();
                    break;
                }
            }
        }
        modelMap.put("defaultFunctions", defaultFunctions);
        return new ApiResult<Map<String,Object>>().setResult(modelMap);
    }


    @RequestMapping("/addGroup")
    public ApiResult<Boolean> addGroup( ServiceInstance service, String groupName,
                                       String fids) {

        ApiResult<Boolean> result = validate(groupName, fids);
        if (result != null) {
            return result;
        }

        logger.info(" service {} add function group {}",
                service.getServiceName(), groupName);

        return serviceFunctionService.addGroup( service, groupName, fids);
    }

    @RequestMapping("/updateGroup")
    public ApiResult<Boolean> updateGroup(ServiceInstance service, int gid,
                                          String groupName, String fids) {

        ApiResult<Boolean> result = validate(groupName, fids);
        if (result != null) {
            return result;
        }

        logger.info(" service {} update function group {}",
                service.getServiceName(), groupName);

        return serviceFunctionService.updateGroup(service, gid, groupName, fids);
    }

    @RequestMapping("/deleteGroup")
    public ApiResult<Boolean> deleteGroup(ServiceInstance service, int gid,
                                          String groupName) {

        logger.info(" service {} delete function group {}",
                service.getServiceName(), groupName);

        return serviceFunctionService.deleteGroup( service, gid, groupName);
    }

    @RequestMapping("/deleteFunction")
    public ApiResult<Boolean> deleteFunction(ServiceInstance service, @RequestParam int fid,
                                             String functionName) {
        logger.info(" delete service {} function  {} gid {}",
                service.getServiceName(), functionName, fid);
        return serviceFunctionService.deleteFunction( service, fid);
    }




    private static ApiResult<Boolean> validateGroupName(String groupName) {
        if (StringUtil.isBlank(groupName) || groupName.length() > 50) {
            if ("默认分组".equals(groupName)) {
                return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage(groupName + "已存在");
            }
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("组名长度【1-50】");
        } else {
            return null;
        }
    }

    private static ApiResult<Boolean> validateFids(String fids) {
        if (StringUtil.isBlank(fids)) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("没有选择函数");
        } else {
            return null;
        }
    }

    private static ApiResult<Boolean> validate(String groupName, String fids) {
        ApiResult<Boolean> apiResult = validateGroupName(groupName);
        if (apiResult != null) {
            return apiResult;
        }
        apiResult = validateFids(fids);
        if (apiResult != null) {
            return apiResult;
        }
        return null;
    }
}
