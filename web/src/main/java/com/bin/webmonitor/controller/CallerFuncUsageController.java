package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.model.dto.FuncConfigDto;
import com.bin.webmonitor.model.vo.CallerFuncUsageVo;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.service.CallerFuncUsageService;
import com.bin.webmonitor.service.OperateRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/callerfuncusage")
public class CallerFuncUsageController  extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CallerFuncUsageController.class);

    @Autowired
    private CallerFuncUsageService callerFuncUsageService;

    @Autowired
    private OperateRecordService operateRecordService;

    /**
     * 已选函数降级.
     */
    @PutMapping(value = "/degrade")
    public ApiResult<String> funcUsageDegrade(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid, @RequestParam("fids") String fids) {
        logger.info(" funcUsageDegrade req cid:{} sid:{}  fids:{}",  cid, sid, fids);
        callerFuncUsageService.degradeFunctions(cid, sid, fids);
        operateRecordService.addOperateRecord( cid, sid, OperateRecordType.CALLERUSAGE_FUNCTION_DEGRADE, "降级函数：" + fids);
        logger.info("funcUsageDegrade resp");
        return okJson();
    }

    /**
     * 函数降级状态查询.
     * 0未降级函数
     * 1已降级函数
     */
    @RequestMapping(value = "/degrade/status", method = RequestMethod.PUT)
    public ApiResult<List<CallerFuncUsageVo>> degradeStatus(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid) {
        logger.info("degradeStatus req cid:{} sid:{}", cid, sid);
        List<CallerFunctionUseage> poList = callerFuncUsageService.getCallerFuncUsageList(cid, sid);
        List<CallerFuncUsageVo> voList = poList.stream().map(callerFuncUsagePo -> {
            CallerFuncUsageVo vo = new CallerFuncUsageVo();
            vo.setFid(callerFuncUsagePo.getFid());
            vo.setFuncName(HtmlUtils.htmlEscape(callerFuncUsagePo.getFname()));
            vo.setDegrade(callerFuncUsagePo.isDegrade());
            return vo;
        }).collect(Collectors.toList());
        logger.info("degradeStatus resp voList.size:{}", voList.size());
        return okJson(voList);
    }

    /**
     * 查看调用方函数使用量.
     */
    @RequestMapping("/list/cid")
    public List<FuncConfigDto> listByCid(@RequestParam("sid") Integer sid, @RequestParam(value = "cid", required = false, defaultValue = "0") Integer cid) {
        logger.info("ServiceFunctionController.listBySid req:{}", sid);
        List<FuncConfigDto> funcConfigList = callerFuncUsageService.listBySid(sid, cid);
        logger.info("ServiceFunctionController.listBySid resp:{}", funcConfigList.size());
        return funcConfigList;
    }

    /**
     * 删除所有相关调用函数.
     */
    @RequestMapping(value = "/all/del", method = RequestMethod.DELETE)
    public ApiResult<String> deleteAllById(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid) {
        logger.info(" deleteById req cid:{} sid:{} ",  cid, sid);
        String delFailIdList = callerFuncUsageService.delAll(cid, sid);
        operateRecordService.addOperateRecord( cid, sid, OperateRecordType.CALLERUSAGE_FUNCTION_DELETE, "删除所有相关调用的函数");
        logger.info("deleteById resp:{} ", delFailIdList);
        return okJson(delFailIdList);
    }

    /**
     * 批量删除调用.
     */
    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public ApiResult<String> deleteById(@RequestParam("cid") Integer cid, @RequestParam("sid") Integer sid, @RequestParam("ids") String ids) {
        logger.info("deleteById req cid:{} sid:{} ids:{}",  cid, sid, ids);
        String delFailIdList = callerFuncUsageService.delBatch(cid, sid, ids);
        operateRecordService.addOperateRecord( cid, sid, OperateRecordType.CALLERUSAGE_FUNCTION_DELETE, "删除部分相关调用的函数：" + ids);
        logger.info("deleteById resp:{} ", delFailIdList);
        return okJson(delFailIdList);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getcallerFuncUsagePoById(@PathVariable("id") Integer id) {
        CallerFunctionUseage callerFuncUsagePo = callerFuncUsageService.findByPrimaryKey(id);
        return new ResponseEntity<>(JsonHelper.toJson(callerFuncUsagePo), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> createcallerFuncUsagePo(@RequestBody CallerFunctionUseage callerFuncUsagePo) {
        Integer id = callerFuncUsageService.create(callerFuncUsagePo);
        return new ResponseEntity<>(JsonHelper.toJson(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseEntity<String> listcallerFuncUsagePos(@PageableDefault Pageable pageable, @RequestBody CallerFunctionUseage callerFuncUsagePo) {
        List<CallerFunctionUseage> callerFuncUsagePos = callerFuncUsageService.selectPage(callerFuncUsagePo, pageable);
        Page<CallerFunctionUseage> page = new PageImpl<>(callerFuncUsagePos, pageable, callerFuncUsageService.selectCount(callerFuncUsagePo));
        return new ResponseEntity<>(JsonHelper.toJson(page), HttpStatus.OK);
    }

    @RequestMapping(value = "/listTimeoutFunction")
    public ApiResult<List<CallerFunctionUseage>> listTimeoutFunction( int cid, int sid) {
        logger.info("op=start_listTimeoutFunction,cid={},sid={}",  cid, sid);
        List<CallerFunctionUseage> res = callerFuncUsageService.getExistTimeoutFunctionUsages(cid, sid);
        return okJson(res);
    }

    @RequestMapping(value = "/listNoTimeoutFunction")
    public ApiResult<List<String>> listNoTimeoutFunction( int cid, int sid) {
        logger.info("op=start_listNoTimeoutFunction,cid={},sid={}", cid, sid);
        List<String> noTimeoutFunctionNames = callerFuncUsageService.getNoTimeoutFunctionNames(cid, sid);
        return okJson(noTimeoutFunctionNames);
    }

}
