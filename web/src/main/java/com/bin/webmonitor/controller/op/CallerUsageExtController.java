package com.bin.webmonitor.controller.op;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.dao.CallerUsageExtDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerUsageExt;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.OperateRecordService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController("opCallerUsageExtController")
@RequestMapping("/op/callerUsageExt")
public class CallerUsageExtController extends BaseController {
    
    @Autowired
    private CallerDao callerDao;
    
    @Autowired
    private CallerUsageExtDao callerUsageExtDao;
    
    @Autowired
    private LocalService localService;
    
    @Autowired
    private OperateRecordService operateRecordService;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @RequestMapping(value = "getCallerUsageExt", method = RequestMethod.GET)
    public ApiResult<CallerUsageExt> getCallerUsageExt( @RequestParam("callerName") String callerName, @RequestParam("serviceName") String serviceName) {
        Caller caller = callerDao.selectByName(callerName);
        ServiceInstance service = localService.getByName(serviceName);
        if (Objects.isNull(caller) || Objects.isNull(service)) {
            logger.info("[getCallerUsageExt_null] caller={},service={}",  caller, service);
            return errorJson("调用方或者服务方不存在");
        }
        CallerUsageExt callerUsage = callerUsageExtDao.queryByCidSid(caller.getId(), service.getId());
        logger.info("op=end_getCallerUsageExt,callerName={},serviceName={},CallerUsageExtPo={}",  callerName, serviceName, callerUsage);
        return okJson(callerUsage);
    }
    
    @RequestMapping(value = "deleteCallerUsageExtById", method = RequestMethod.GET)
    public ApiResult<String> deleteCallerUsageExtById(@RequestParam("id") int id, @RequestParam("date") String date) {
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");
        CallerUsageExt callerUsage = callerUsageExtDao.queryById(id);
        if (Objects.isNull(callerUsage)) {
            logger.info("[deleteCallerUsageExtById_null] id={}",  id);
            return errorJson("数据不存在");
        }
        int n = callerUsageExtDao.deleteById(id);
        
        operateRecordService.addOperateRecord( callerUsage.getCid(), callerUsage.getSid(), OperateRecordType.CALLERUSAGE_UPDATE_PASS, "删除调用关系扩展信息:" + callerUsage);
        
        CallerUsageExt afterDelCallerUsage = callerUsageExtDao.queryById(id);
        Map<String, Object> res = new HashMap<>();
        res.put("beforeDelete", callerUsage);
        res.put("afterDelete", afterDelCallerUsage);
        res.put("DeleteRows", n);
        logger.info("op=end_deleteCallerUsageExtById,id={},res={}",  id, res);
        return okJson(res);
    }
}
