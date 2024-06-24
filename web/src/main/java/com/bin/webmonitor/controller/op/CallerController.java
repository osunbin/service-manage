package com.bin.webmonitor.controller.op;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.bin.webmonitor.common.BaseError;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.environment.EnvManager;
import com.bin.webmonitor.environment.EnvironmentEnum;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.CallerUsageExtDao;
import com.bin.webmonitor.repository.dao.CircuitBreakConfigDao;
import com.bin.webmonitor.repository.dao.CircuitBreakEventDao;
import com.bin.webmonitor.repository.dao.CircuitBreakMonitorDao;
import com.bin.webmonitor.repository.dao.ClientConfigDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.CallerUsageExt;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.CircuitBreakConfig;
import com.bin.webmonitor.repository.domain.CircuitBreakEvent;
import com.bin.webmonitor.repository.domain.CircuitBreakMonitor;
import com.bin.webmonitor.service.OperateRecordService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("opCallerController")
@RequestMapping("/op/caller")
public class CallerController extends BaseController {

    @Autowired
    private CallerDao callerDao;

    @Autowired
    private CallerUsageDao callerUsageDao;

    @Autowired
    private EnvManager envManager;

    @Autowired
    private LocalService localService;


    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;

    @Autowired
    private CircuitBreakConfigDao circuitBreakConfigDao;

    @Autowired
    private CircuitBreakEventDao circuitBreakEventDao;

    @Autowired
    private CircuitBreakMonitorDao circuitBreakMonitorDao;

    @Autowired
    private CallerUsageExtDao callerUsageExtDao;

    @Autowired
    private ClientConfigDao clientConfigDao;



    @Autowired
    private OperateRecordService operateRecordService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "getCallerById", method = RequestMethod.GET)
    public ApiResult<String> getCallerById( @RequestParam("id") int id) {
        Caller caller = new Caller();
        caller.setId(id);

        List<Caller> callers = callerDao.selectPage(caller, PageRequest.of(0, 100));

        logger.info("op=end_getCallerById,id={},callers={}", id, callers);
        return okJson(callers);
    }

    @RequestMapping(value = "getCallerByName", method = RequestMethod.GET)
    public ApiResult<String> getCallerByName( @RequestParam("callerName") String callerName) {
        Caller caller = new Caller();
        caller.setCallerName(callerName);

        List<Caller> callers = callerDao.selectPage(caller, PageRequest.of(0, 100));

        logger.info("op=end_getCallerByName,callerName={},callers={}",  callerName, callers);
        return okJson(callers);
    }

    @RequestMapping(value = "getCallerByKey", method = RequestMethod.GET)
    public ApiResult<String> getCallerByKey( @RequestParam("callerKey") String callerKey) {
        Caller caller = new Caller();
        caller.setCallerKey(callerKey);

        List<Caller> callers = callerDao.selectPage(caller, PageRequest.of(0, 100));

        logger.info("op=end_getCallerByKey,callerKey={},callers={}",  callerKey, callers);
        return okJson(callers);
    }

    @RequestMapping(value = "forceDeleteCaller", method = RequestMethod.GET)
    public ApiResult<String> forceDeleteCaller( @RequestParam("callerName") String callerName,
            @RequestParam("date") String date) {
        logger.info("op=start_forceDeleteCaller,callerName={},date={}", callerName, date);
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");

        Caller queryCaller = new Caller();
        queryCaller.setCallerName(callerName);

        // 1.获得调用方信息
        List<Caller> callers = callerDao.selectPage(queryCaller, PageRequest.of(0, 100));

        // 2.找到目标调用方
        Caller caller = null;
        for (Caller po : callers) {
            if (Objects.equals(po.getCallerName(), callerName)) {
                caller = po;
                break;
            }
        }
        if (Objects.isNull(caller)) {
            return errorJson(String.format("调用方[%s]不存在！！！", callerName));
        }

        // 3.获取该调用方的调用关系
        CallerUseage queryCallerUseage = new CallerUseage();
        queryCallerUseage.setCid(caller.getId());
        List<CallerUseage> callerUseages = callerUsageDao.selectPage(queryCallerUseage, PageRequest.of(0, Integer.MAX_VALUE));

        // 4.删除所有调用关系
        for (CallerUseage item : callerUseages) {
            int deleteCount = callerUsageDao.deleteByPrimaryKey(item.getId());
            logger.info("[delete_callerUsage]CallerUseage={},deleteCount={}", item, deleteCount);
        }

        //获取该调用方所有调用的函数
        CallerFunctionUseage queryCallerFunctionUseage = new CallerFunctionUseage();
        queryCallerFunctionUseage.setCid(caller.getId());
        List<CallerFunctionUseage> callerFuncUsagePos = callerFuncUsageDao.selectPage(queryCallerFunctionUseage, PageRequest.of(0, Integer.MAX_VALUE));

        //所有调用的函数
        for (CallerFunctionUseage callerFuncUsagePo : callerFuncUsagePos) {
            int deleteCount = callerFuncUsageDao.deleteByPrimaryKey(callerFuncUsagePo.getId());
            logger.info("[delete_callerFuncUsagePo]callerFuncUsagePo={},deleteCount={}", callerFuncUsagePo, deleteCount);
        }

        //删除所有熔断信息
        List<CircuitBreakConfig> CircuitBreakConfigs = circuitBreakConfigDao.queryCircuitBreakConfigs(caller.getId(), null, null);
        for (CircuitBreakConfig CircuitBreakConfig : CircuitBreakConfigs) {
            int deleteCount = circuitBreakConfigDao.deleteById(CircuitBreakConfig.getId());
            logger.info("[delete_CircuitBreakConfig]CircuitBreakConfig={},deleteCount={}", CircuitBreakConfig, deleteCount);
        }
        List<CircuitBreakEvent> CircuitBreakEvents = circuitBreakEventDao.queryCircuitBreakEvents(caller.getId(), null, null, null, PageRequest.of(0, Integer.MAX_VALUE));
        for (CircuitBreakEvent CircuitBreakEvent : CircuitBreakEvents) {
            int deleteCount = circuitBreakEventDao.deleteById(CircuitBreakEvent.getId());
            logger.info("[delete_CircuitBreakEvent]CircuitBreakEvent={},deleteCount={}", CircuitBreakEvent, deleteCount);
        }
        List<CircuitBreakMonitor> CircuitBreakMonitors = circuitBreakMonitorDao.queryCircuitBreakMonitors(caller.getId(), null, null, PageRequest.of(0, Integer.MAX_VALUE));
        for (CircuitBreakMonitor CircuitBreakMonitor : CircuitBreakMonitors) {
            int deleteCount = circuitBreakMonitorDao.deleteById(CircuitBreakMonitor.getId());
            logger.info("[delete_CircuitBreakMonitor]CircuitBreakMonitor={},deleteCount={}", CircuitBreakMonitor, deleteCount);
        }

        //删除调用关系扩展表中数据
        List<CallerUsageExt> CallerUsageExts = callerUsageExtDao.queryByCidOrSid(caller.getId(), null);
        for (CallerUsageExt CallerUsageExt : CallerUsageExts) {
            int deleteCount = callerUsageExtDao.deleteById(CallerUsageExt.getId());
            logger.info("[delete_CallerUsageExt]CallerUsageExt={},deleteCount={}", CallerUsageExt, deleteCount);
        }

        //删除调用方配置信息
        int deleteConfigCount = clientConfigDao.deleteByCid(caller.getId());
        logger.info("[delete_config]deleteConfigCount={}", deleteConfigCount);


        // 5.删除调用方信息
        int deleteCallerCount = callerDao.deleteByPrimaryKey(caller.getId());
        logger.info("[delete_caller]Caller={},deleteCallerCount={}", caller, deleteCallerCount);

        // 6.获取删除后的调用方
        List<Caller> afterCallers = callerDao.selectPage(queryCaller, PageRequest.of(0, 100));

        // 7.获取删除后的调用关系
        List<CallerUseage> afterCallerUseages = callerUsageDao.selectPage(queryCallerUseage, PageRequest.of(0, Integer.MAX_VALUE));

        operateRecordService.addOperateRecord(caller.getId(), null, OperateRecordType.CALLER_DELETE,
                "通过op接口强制删除调用方:" + caller);
        Map<String, Object> res = new HashMap<>();
        res.put("Caller", caller);
        res.put("afterCallers", afterCallers);
        res.put("CallerUseages", callerUseages);
        res.put("afterCallerUseages", afterCallerUseages);

        logger.info("op=end_forceDeleteCaller,callerName={},date={},res={}",  callerName, date, res);
        return okJson(res);
    }

    /**
     * 更新调用方的key
     */
    @RequestMapping(value = "updateCallerKey")
    public ApiResult<Map<String, Caller>> updateCallerKey(@RequestParam("callerName") String callerName,
            @RequestParam("oldCallerKey") String oldCallerKey, @RequestParam("newCallerKey") String newCallerKey, @RequestParam("date") String date) {
        log.info("op=start_updateCallerKey,callerName={},oldCallerKey={},newCallerKey={},date={}",  callerName, oldCallerKey,
                newCallerKey, date);

        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");



        // 根据oldCallerKey查询调用方信息
        Caller queryCaller = new Caller();
        queryCaller.setCallerKey(oldCallerKey);
        List<Caller> callers = callerDao.selectPage(queryCaller, PageRequest.of(0, 10));
        if (CollectionUtils.isEmpty(callers)) {
            return errorJson(BaseError.ID_NOT_EXIST.code(), String.format("不存在的callerKey【%s】", oldCallerKey));
        }
        if (callers.size() > 1) {
            return errorJson(BaseError.PARAM_WRONG.code(), String.format("存在多个调用方的callerKey为【%s】", oldCallerKey));
        }

        Caller beforeCaller = callers.get(0);
        // 检查callerName是否一致
        if (!Objects.equals(callerName, beforeCaller.getCallerName())) {
            return errorJson(BaseError.PARAM_WRONG.code(), String.format("非法的callerName【%s】，callerKey【%s】对应的callerName应为【%s】", callerName,
                                                                  oldCallerKey, beforeCaller.getCallerName()));
        }

        // 检查newCallerKey是否已被使用
        queryCaller = new Caller();
        queryCaller.setCallerKey(newCallerKey);
        List<Caller> newCallerKeyPos = callerDao.selectPage(queryCaller, PageRequest.of(0, 10));
        if (!CollectionUtils.isEmpty(newCallerKeyPos)) {
            List<String> duplicateCallerNames = newCallerKeyPos.stream().map(Caller::getCallerName).collect(Collectors.toList());
            return errorJson(BaseError.PARAM_WRONG.code(), String.format("已经有调用方【%s】使用newCallerKey【%s】", duplicateCallerNames, newCallerKey));
        }

        // 更新newCallerKey
        Caller caller = new Caller();
        caller.setId(beforeCaller.getId());
        caller.setCallerKey(newCallerKey);
        int updateCount = callerDao.updateByPrimaryKeySelective(caller);

        // 根据newCallerKey查询是否更新成功
        queryCaller = new Caller();
        queryCaller.setCallerKey(newCallerKey);
        List<Caller> afterCallers = callerDao.selectPage(queryCaller, PageRequest.of(0, 10));
        if (CollectionUtils.isEmpty(afterCallers)) {
            log.error("[ERROR-updateCallerKey-unknown]");
            return errorJson(BaseError.SERVICE_ERROR.code(), String.format("遇到未知错误，更新失败。callerName=【%s】,oldCallerKey=【%s】,oldCallerKey=【%s】",
                                                                    callerName, oldCallerKey, newCallerKey));
        }
        operateRecordService.addOperateRecord( beforeCaller.getId(), null, OperateRecordType.CALLER_UPDATE,
                "通过op接口将调用方" + callerName + "的callerkey=" + oldCallerKey + "更新为callerkey=" + newCallerKey);
        Caller afterCaller = afterCallers.get(0);

        Map<String, Caller> res = new HashMap<>();
        res.put("before", beforeCaller);
        res.put("after", afterCaller);

        log.info("op=end_updateCallerKey,callerName={},oldCallerKey={},newCallerKey={},beforeCaller={},afterCaller={},updateCount={}",
                  callerName, oldCallerKey, newCallerKey, beforeCaller, afterCaller, updateCount);
        return okJson(afterCaller.getCallerKey());
    }

}
