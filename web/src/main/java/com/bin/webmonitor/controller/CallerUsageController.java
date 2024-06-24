package com.bin.webmonitor.controller;

import com.bin.webmonitor.command.RejectCommand;
import com.bin.webmonitor.common.BaseError;
import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.model.vo.CallerConfigVo;
import com.bin.webmonitor.model.vo.CallerUsageVo;
import com.bin.webmonitor.model.vo.IdNameVo;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.naming.NamingProxy;
import com.bin.webmonitor.naming.ServiceNode;
import com.bin.webmonitor.naming.model.Response;
import com.bin.webmonitor.service.CallerService;
import com.bin.webmonitor.service.CallerUsageService;
import com.bin.webmonitor.service.OperateRecordService;
import com.bin.webmonitor.component.SendCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bin.webmonitor.common.BaseError.ALREADY_UPDATE;
import static com.bin.webmonitor.common.BaseError.CONDITION_ILLEGAL;
import static com.bin.webmonitor.common.BaseError.ID_NOT_EXIST;
import static com.bin.webmonitor.common.Constants.PAGE_MAX;
import static com.bin.webmonitor.common.Constants.PAGE_ONE;
import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/callerusage")
public class CallerUsageController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(CallerUsageController.class);

    @Autowired
    private CallerUsageService callerUsageService;



    @Autowired
    private SendCommand sendCommand;

    @Autowired
    private LocalService localService;

    @Autowired
    private CallerService callerService;

    @Autowired
    private NamingProxy namingProxy;



    @Autowired
    private CallerDao callerDao;

    @Autowired
    private OperateRecordService operateRecordService;

    @RequestMapping(value = "/callers/{sid}", method = RequestMethod.GET)
    public ApiResult<List<IdNameVo>> callerNames(@PathVariable("sid") Integer sid) {
        List<IdNameVo> names = callerUsageService.getCallerNames(sid);
        return okJson(names);
    }

    @RequestMapping(value = "/svcs/{cid}", method = RequestMethod.GET)
    public ApiResult<List<IdNameVo>> svcNames(@PathVariable("cid") Integer cid) {
        List<IdNameVo> names = callerUsageService.getSvcNames(cid);
        return okJson(names);
    }

    /**
     * 调用方相关的服务名称列表
     */
    @RequestMapping(value = "/caller/svcs", method = RequestMethod.GET)
    public ApiResult<?> getCallerSvcs(@RequestParam("cid") Integer cid) {
        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setCid(cid);
        List<CallerUseage> poList = callerUsageService.selectPage(callerUseage, PAGE_MAX);
        List<IdNameVo> idNameVos = poList.stream().map((Function<CallerUseage, IdNameVo>) input -> {
            Integer sid = input.getSid();
            ServiceInstance byId = localService.getById(sid);
            return new IdNameVo(sid, byId.getServiceName());
        }).collect(Collectors.toList());
        return okJson(idNameVos);
    }

    /**
     * 调用方详情左侧服务名称列表. sid为0代表全部
     */
    @RequestMapping(value = "/svcs/detail", method = RequestMethod.GET)
    public ResponseEntity<String> svcsDetailList(@PageableDefault Pageable pageable, @RequestParam("cid") Integer cid, @RequestParam(value = "sid", defaultValue = "0") Integer sid) {
        log.info("svcsDetailList req cid:{} sid:{}", cid, sid);
        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setCid(cid);
        if (sid != 0) {
            callerUseage.setSid(sid);
        }
        Page<CallerUsageVo> page = callerUsageService.selectVoPage(callerUseage, pageable);
        log.info("svcsDetailList resp:{}", page.getSize());
        return new ResponseEntity<>(JsonHelper.toJson(page), HttpStatus.OK);
    }


    /**
     * 1、检测调用关系是否存在.
     */
    @RequestMapping(value = "/is_exist", method = RequestMethod.GET)
    public ApiResult<Boolean> isExisting(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid) {
        logger.info("isExisting req cid:{} sid:{}", cid, sid);
        boolean isExist = callerUsageService.isExist(cid, sid);
        logger.info("isExisting resp:{}", isExist);
        if (isExist) {
            return okJson(Boolean.TRUE, "当前调用关系已存在");
        }
        String serviceName = localService.getServiceNameById(sid);

        Response<List<ServiceNode>> callerNodes = namingProxy.getNodes(serviceName);
        if (CollectionUtil.isEmpty(callerNodes.getData())) {
            return okJson(Boolean.TRUE, "控制中心无服务方节点, 请协助服务方上线");
        }
        List<ServiceNode> data = callerNodes.getData();
        List<ServiceNode> onlineInServiceList = data.stream().filter(serviceNode ->
                serviceNode.isRunning() && serviceNode.isInService()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(onlineInServiceList)) {
            return okJson(Boolean.TRUE, "服务方无在线且可调度的节点");
        }
        return okJson(Boolean.FALSE);
    }

    /**
     * 调用方管理-调用方详情-配置详情
     */
    @RequestMapping(value = "/config/detail", method = RequestMethod.GET)
    public ApiResult<CallerConfigVo> getConfigDetail(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid) {
        logger.info("getConfigDetail req cid:{} sid:{}", cid, sid);
        CallerConfigVo vo = callerUsageService.genCallerConfig(cid, sid);
        logger.info("getConfigDetail resp:{}", vo.toString());
        return okJson(vo);
    }

    /**
     * 服务降级
     */
    @RequestMapping(value = "/service/degrade", method = RequestMethod.GET)
    public ApiResult<CallerConfigVo> serviceDegrade(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid,
                                                    @RequestParam("degrade") Boolean degrade) {

        logger.info("  serviceDegrade req cid:{} sid:{}",  cid, sid);

        if (!callerService.isCallerOwner(cid)) {
            return errorJson(BaseError.NO_PERMISSION.code(), BaseError.NO_PERMISSION.getMessage());
        }
        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setCid(cid);
        callerUseage.setSid(sid);
        List<CallerUseage> callerUseages = callerUsageService.selectPage(callerUseage, PAGE_ONE);
        CallerUseage po = CollectionUtil.getFirstOrNull(callerUseages);
        if (null == po) {
            return errorJson(ID_NOT_EXIST.code(), "调用关系不存在");
        }
        if (po.isDegrade()) {
            return errorJson(ALREADY_UPDATE.code(), "服务状态已设置");
        }
        po.setDegrade(degrade);
        int ret = callerUsageService.updateByPrimaryKey(po);
        logger.info("CallerUsageController.serviceDegrade resp:{}", ret);
        sendCommand.sendDegradeCommand(sid, cid);
        ServiceInstance service = localService.getById(sid);
        Caller caller = callerDao.selectByPrimaryKey(cid);
        operateRecordService.addOperateRecord(cid, sid, OperateRecordType.CALLERUSAGE_SERVICE_DEGRADE, caller.getCallerName() + "调用方" + service.getServiceName() + "服务降级");
        if (ret == 1) {
            return okJson();
        }
        return errorJson(ALREADY_UPDATE.code(), "服务已降级");
    }

    /**
     * 删除调用方与服务方的调用关系。
     */
    @RequestMapping(value = "/auth/del", method = RequestMethod.DELETE)
    public ApiResult<String> deleteCallerUsage(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid)
            throws IOException {

        logger.info(" deleteCallerUsage req cid:{} sid:{} ", cid, sid);

        Caller caller = callerDao.selectByPrimaryKey(cid);
        checkArgument(null != caller, "调用方Id不存在");
        ServiceInstance serviceById = localService.getById(sid);
        checkArgument(null != serviceById, "服务方Id不存在");
        List<CallerUseage> callerUsageList = callerUsageService.getCallerUsageList(cid, sid);
        CallerUseage callerUseage = CollectionUtil.getFirstOrNull(callerUsageList);
        if (null == callerUseage) {
            return errorJson(CONDITION_ILLEGAL.code(), "调用关系不存在");
        }

        ServiceInstance service = localService.getById(sid);

        // 1.删除调用关系相关信息
        int deleteCount = callerUsageService.deleteCallerUsage(callerUseage.getId(), cid, sid);
        // 2.给服务方发送变更通知
        sendCommand.sendService(service.getServiceName(), new RejectCommand().setCallerKey(caller.getCallerKey()));


        operateRecordService.addOperateRecord( cid, sid, OperateRecordType.CALLERUSAGE_DELETE_APPLY, "申请删除" + caller.getCallerName() + "->" + serviceById.getServiceName() + "的调用关系：" + callerUseage);
        logger.info("deleteCallerUsage deleteCount:{}", deleteCount);
        if (deleteCount > 0) {
            return okJson();
        }
        return errorJson(ID_NOT_EXIST.code(), ID_NOT_EXIST.getMessage());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ApiResult<CallerUseage> getById(@PathVariable("id") Integer id) {
        logger.info("getById req:{}", id);
        CallerUseage callerUseage = callerUsageService.findByPrimaryKey(id);
        logger.info("getById resp:{}", callerUseage.toString());
        return okJson(callerUseage);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult<Integer> create(@RequestBody CallerUseage callerUseage) {
        logger.info("create req:{}", callerUseage.toString());
        callerUsageService.createSelective(callerUseage);
        Integer id = callerUseage.getId();
        logger.info("create resp:{}", id);
        return okJson(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ApiResult<String> updateByIdSelective(@PathVariable("id") Integer id, CallerUseage po) {
        logger.info("updateByIdSelective req cid:{} sid:{} ",  po.getCid(), po.getSid());
        po.setId(id);
        Integer result = callerUsageService.updateByPrimaryKeySelective(po);
        if (result == 1) {
            return okJson();
        }
        return errorJson(ID_NOT_EXIST.code(), ID_NOT_EXIST.getMessage());
    }

}
