package com.bin.webmonitor.controller.op;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.controller.BaseController;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.CallerUsageExtDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.CallerUsageExt;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.OperateRecordService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController("opCallerUsageController")
@RequestMapping("/op/callerUsage")
public class CallerUsageController extends BaseController {

    @Autowired
    private CallerDao callerDao;
    @Autowired
    private ServiceDao serviceDao;
    @Autowired
    private CallerUsageDao callerUsageDao;
    @Autowired
    private CallerUsageExtDao callerUsageExtDao;
    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;
    @Autowired
    private OperateRecordService operateRecordService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "getCallerUsages", method = RequestMethod.GET)
    public ApiResult<String> getCallerUsages( @RequestParam(name = "callerName", required = false) String callerName,
                                             @RequestParam(name = "serviceName", required = false) String serviceName) {
        Caller caller = Objects.isNull(callerName) ? null : callerDao.selectByName(callerName);
        ServiceInstance service = Objects.isNull(serviceName) ? null : serviceDao.selectByName(serviceName);

        Integer cid = Objects.isNull(caller) ? null : caller.getId();
        Integer sid = Objects.isNull(service) ? null : service.getId();
        List<CallerUseage> callerUseages = getCallerUsages(cid, sid);

        //将扩展信息追回到description字段
        for (CallerUseage item : callerUseages) {
            CallerUsageExt CallerUsageExt = callerUsageExtDao.queryByCidSid(item.getCid(), item.getSid());
            // 借用description字段
            String description = item.getDescription() + String.format("    扩展信息：%s", CallerUsageExt); 
            item.setDescription(description);
        }

        logger.info("op=end_getCallerUsages,callerName={},serviceName={},caller={},service={},CallerUseages={}",
                    callerName, serviceName, caller, service, callerUseages);

        Map<String, Object> res = new HashMap<>();
        res.put("caller", caller);
        res.put("service", service);
        res.put("callerUsages", callerUseages);
        return okJson(res);
    }

    @RequestMapping(value = "forceDeleteCallerUsage", method = RequestMethod.GET)
    public ApiResult<String> forceDeleteCallerUsage( @RequestParam("callerName") String callerName,
                                                    @RequestParam(name = "serviceName", required = false) String serviceName, @RequestParam("date") String date) {
        logger.info("op=start_forceDeleteCallerUsage,callerName={},serviceName={},date={}",  callerName,
                serviceName, date);
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");

        Caller caller = callerDao.selectByName(callerName);
        if (Objects.isNull(caller)) {
            return errorJson(String.format("调用方[%s]不存在！", callerName));
        }

        ServiceInstance service = serviceDao.selectByName(serviceName);
        if (Objects.isNull(caller)) {
            return errorJson(String.format("服务方[%s]不存在！", service));
        }

        CallerUseage callerUseage = callerUsageDao.selectByCidSid(caller.getId(), service.getId());
        int deleteCount = 0;
        if (Objects.nonNull(callerUseage)) {
            deleteCount = callerUsageDao.deleteByPrimaryKey(callerUseage.getId());
        }
        CallerUseage afterCallerUseage = callerUsageDao.selectByCidSid(caller.getId(), service.getId());

        operateRecordService.addOperateRecord(caller.getId(), service.getId(), OperateRecordType.CALLERUSAGE_DELETE_PASS,
                "强制删除调用关系" + callerUseage);
        Map<String, Object> res = new HashMap<>();
        res.put("caller", caller);
        res.put("service", service);
        res.put("callerUsages", callerUseage);
        res.put("afterCallerUseage", afterCallerUseage);
        res.put("deleteCount", deleteCount);
        return okJson(res);
    }

    /**
     * 将调用关系从源调用方复制到目的调用方，同时也复制调用函数信息。
     * 若复制的关系目的调用方已经存在，则本条调用关系复制失败，调用函数复制亦同。
     * @param fromCallerName 源调用放
     * @param toCallerName 目的调用方
     * @return 返回信息
     */
    @RequestMapping(value = "copyCallerUsages", method = RequestMethod.GET)
    public ApiResult<String> copyCallerUsages(@RequestParam("fromCallerName") String fromCallerName,
            @RequestParam("toCallerName") String toCallerName, @RequestParam("date") String date) {
        logger.info("op=start_copyCallerUsage,fromCallerName={},toCallerName={},date={}",  fromCallerName, toCallerName, date);
        Preconditions.checkArgument(StringUtil.checkDate(date), "安全检查不通过");
        if (StringUtil.isEmpty(fromCallerName) || StringUtil.isEmpty(toCallerName)) {
            return errorJson("复制调用方调用关系时，复制源或者复制目的地调用方名称为空");
        }
        if (fromCallerName.equals(toCallerName)) {
            return errorJson("复制调用方调用关系时，复制源或者复制目的地调用方名称相同");
        }

        Caller fromCaller = callerDao.selectByName(fromCallerName);
        Caller toCaller = callerDao.selectByName(toCallerName);
        if (Objects.isNull(fromCaller)) {
            return errorJson("复制调用方调用关系时，复制源的调用方不存在");
        }
        if (Objects.isNull(toCaller)) {
            return errorJson("复制调用方调用关系时，复制目的地的调用方不存在");
        }

        //1.获取复制源的调用方信息
        List<CallerUseage> callerUsages = getCallerUsages(fromCaller.getId(), null);
        int callerUsageSuccessCount = 0;
        int callerUsageFailCount = 0;

        //2.复制调用关系到新调用方
        List<CallerUseage> failedCallerUsages = new ArrayList<>();
        for (CallerUseage callerUsage : callerUsages) {
            callerUsage.setCid(toCaller.getId());
            callerUsage.setCreateTime(new Date());
            callerUsage.setUpdateTime(new Date());

            int insertCount = callerUsageDao.insert(callerUsage);
            if (insertCount != 0) {
                callerUsageSuccessCount++;
                logger.info("[succeed_copyCallerUsage]fromCaller={},toCaller={},callerUsage={}", fromCaller, toCaller, callerUsage);
            } else {
                failedCallerUsages.add(callerUsage);
                callerUsageFailCount++;
                logger.warn("[failed_copyCallerUsage]fromCaller={},toCaller={},callerUsage={}", fromCaller, toCaller, callerUsage);
            }
        }

        //3.复制调用函数的信息
        List<CallerFunctionUseage> callerFuncUsages = getCallerFuncUsages(fromCaller.getId(), null, null);
        int callerFuncUsageSuccessCount = 0;
        int callerFuncUsageFailCount = 0;

        List<CallerFunctionUseage> failedCallerFuncUsages = new ArrayList<>();
        for (CallerFunctionUseage callerFuncUsage : callerFuncUsages) {
            callerFuncUsage.setCid(toCaller.getId());
//            callerFuncUsage.setId(null);
            callerFuncUsage.setCreateTime(new Date());
            callerFuncUsage.setUpdateTime(new Date());

            int insertCount = callerFuncUsageDao.insert(callerFuncUsage);
            if (insertCount != 0) {
                callerFuncUsageSuccessCount++;
                logger.info("[succeed_copyCallerFuncUsage]fromCaller={},toCaller={},callerFuncUsage={}", fromCaller, toCaller, callerFuncUsage);
            } else {
                failedCallerFuncUsages.add(callerFuncUsage);
                callerFuncUsageFailCount++;
                logger.warn("[failed_copyCallerFuncUsage]fromCaller={},toCaller={},callerFuncUsage={}", fromCaller, toCaller, callerFuncUsage);
            }
        }

        List<CallerFunctionUseage> newCallerFuncUsage = getCallerFuncUsages(toCaller.getId(), null, null);
        List<CallerUseage> newCallerUsages = getCallerUsages(toCaller.getId(), null);

        operateRecordService.addOperateRecord( toCaller.getId(), null, OperateRecordType.CALLERUSAGE_REGISTER_PASS,
                "将调用关系从" + fromCallerName + "复制到" + toCallerName);
        Map<String, Object> res = new HashMap<>();
        res.put("newCallerUsages", newCallerUsages);
        res.put("newCallerFunctUsages", newCallerFuncUsage);
        res.put("failedCallerUsages", failedCallerUsages);
        res.put("failedCallerFuncUsages", failedCallerFuncUsages);
        res.put("callerUsageSuccessCount", callerUsageSuccessCount);
        res.put("callerUsageFailCount", callerUsageFailCount);
        res.put("callerFuncUsageSuccessCount", callerFuncUsageSuccessCount);
        res.put("callerFuncUsageFailCount", callerFuncUsageFailCount);
        return okJson(res);
    }

    private List<CallerUseage> getCallerUsages( Integer cid,  Integer sid) {
        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setCid(cid);
        callerUseage.setSid(sid);

        List<CallerUseage> res = callerUsageDao.selectPage(callerUseage, PageRequest.of(0, Integer.MAX_VALUE));
        return res;
    }

    private List<CallerFunctionUseage> getCallerFuncUsages( Integer cid,  Integer sid,  Integer fid) {
        CallerFunctionUseage callerFuncUsagePo = new CallerFunctionUseage();
        callerFuncUsagePo.setCid(cid);
        callerFuncUsagePo.setSid(sid);
        callerFuncUsagePo.setFid(fid);

        List<CallerFunctionUseage> res = callerFuncUsageDao.selectPage(callerFuncUsagePo, PageRequest.of(0, Integer.MAX_VALUE));
        return res;
    }

}
