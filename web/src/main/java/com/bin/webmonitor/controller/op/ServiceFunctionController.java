package com.bin.webmonitor.controller.op;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("opServiceFunctionController")
@RequestMapping("/op/service/function")
public class ServiceFunctionController extends BaseController {
    
    private Logger logger = LoggerFactory.getLogger(ServiceFunctionController.class);
    
    @Autowired
    private ServiceFunctionDao serviceFunctionDao;
    
    @Autowired
    private LocalService localService;
    
    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;
    
    @RequestMapping(value = "forceDeleteFunction", method = RequestMethod.GET)
    public ApiResult<Map<String, String>> forceDeleteFunction(String functionName, String serviceName, String date) {
        logger.info("op=start_forceDeleteFunction,functionName={},serviceName={}", functionName, serviceName);
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");
        
        Map<String, String> res = new HashMap<>();
        
        ServiceInstance service = localService.getByName(serviceName);
        if (Objects.isNull(service)) {
            return errorJson(String.format("服务%s不存在", serviceName));
        }
        
        ServiceFunction serviceFunction = serviceFunctionDao.getServiceFunctionsBySidAndFname(service.getId(), functionName);
        if (Objects.isNull(serviceFunction)) {
            return errorJson(String.format("服务%s不存在函数%s", serviceName, functionName));
        }
        
        CallerFunctionUseage requestCallerFuncUsage = new CallerFunctionUseage();
        requestCallerFuncUsage.setFid(serviceFunction.getId());
        List<CallerFunctionUseage> callerFuncUsagePos = callerFuncUsageDao.selectPage(requestCallerFuncUsage, Constants.PAGE_MAX);
        
        res.put("ServiceFunctionBefore", serviceFunction.toString());
        res.put("callerFunctionUsageBefore", callerFuncUsagePos.toString());
        
        for (CallerFunctionUseage callerFuncUsagePo : callerFuncUsagePos) {
            callerFuncUsageDao.deleteByPrimaryKey(callerFuncUsagePo.getId());
        }
        
        serviceFunctionDao.deleteById(serviceFunction.getId());
        
        ServiceFunction afterServiceFunction = serviceFunctionDao.getServiceFunctionsBySidAndFname(service.getId(), functionName);
        List<CallerFunctionUseage> afterCallerFunctionUseages = callerFuncUsageDao.selectPage(requestCallerFuncUsage, Constants.PAGE_MAX);
        
        res.put("ServiceFunctionAfter", Objects.isNull(afterServiceFunction) ? "" : afterServiceFunction.toString());
        res.put("callerFunctionUsageAfter", CollectionUtils.isEmpty(afterCallerFunctionUseages) ? "" : afterCallerFunctionUseages.toString());
        
        logger.info("op=end_forceDeleteFunction,res={}",  res);
        return okJson(res);
    }
}
