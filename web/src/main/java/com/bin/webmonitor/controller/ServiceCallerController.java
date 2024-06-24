package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.component.ServiceCallerService;
import com.bin.webmonitor.model.dto.CallerConfigDto;
import com.bin.webmonitor.model.vo.CallerConfigVo;
import com.bin.webmonitor.model.vo.ServiceCallerVO;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.CallerUsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/caller")
public class ServiceCallerController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(ServiceCallerController.class);
    @Autowired
    private ServiceCallerService serviceCallerService;
    @Autowired
    private CallerUsageService callerUsageService;




    @RequestMapping("/list")
    public DataGrid<ServiceCallerVO> list( @PageableDefault Pageable pageable, int sid, @RequestParam(value = "cid", defaultValue = "0") int cid, @RequestParam(value = "orgId", defaultValue = "0") int orgId, String sf) {
        return serviceCallerService.list(pageable, sid, cid, sf, orgId);
    }

    @RequestMapping("/updateGroup")
    public ApiResult<Boolean> updateGroup( ServiceInstance service, int cid, int gid) {
        logger.info("change service {} cid {} group {}", service.getServiceName(), cid, gid);
        return serviceCallerService.updateGroup( service, cid, gid);
    }


    @RequestMapping("/updateReject")
    public ApiResult<Boolean> updateReject( ServiceInstance service, int cid, boolean reject) {
        logger.info(" change service {} cid {}  reject {}",  service.getServiceName(), cid, reject);
        return serviceCallerService.updateReject( service, cid, reject);
    }


    /**
     * 调用方管理-调用方详情-配置详情
     */
    @RequestMapping(value = "/config/detail", method = RequestMethod.GET)
    public ApiResult<CallerConfigVo> getConfigDetail(Integer cid, Integer sid) {
        CallerConfigVo vo = callerUsageService.genCallerConfig(cid, sid);
        return new ApiResult<CallerConfigVo>().setResult(vo).setCode(Constants.SUCCESS);
    }

    @RequestMapping("/update/config")
    public ApiResult<Boolean> updateConfig( @RequestBody CallerConfigDto callerConfigDto) {

        logger.info("update caller usage {}",  callerConfigDto);

        callerUsageService.updateCaller(callerConfigDto);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }




}
