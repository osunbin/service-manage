package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.BaseError;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.DiffUtil;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.environment.EnvManager;
import com.bin.webmonitor.environment.EnvironmentEnum;
import com.bin.webmonitor.model.vo.CallerVo;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.service.CallerService;
import com.bin.webmonitor.service.OperateRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bin.webmonitor.common.BaseError.CONDITION_ILLEGAL;
import static com.bin.webmonitor.common.BaseError.ID_NOT_EXIST;
import static com.bin.webmonitor.common.BaseError.SUCCESS;
import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/caller")
public class CallerController extends BaseController {


    private static final Logger log = LoggerFactory.getLogger(CallerController.class);

    @Autowired
    private CallerService callerService;

    @Autowired
    private EnvManager envManager;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private CallerDao callerDao;



    /**
     * 下载秘钥.
     */
    @GetMapping(value = "/auth/key")
    public ResponseEntity<byte[]> getSecret(@RequestParam("cid") Integer cid) {
        String callerKey = callerService.getCallerKey(cid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "key.key");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(callerKey.getBytes(), headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{cid}")
    public ApiResult<CallerVo> getCallerById(@PathVariable("cid") Integer id) {
        log.info("getSecret req:{}", id);
        CallerVo callerVo = callerService.findByPrimaryKey(id);
        log.info("getSecret resp:{}", callerVo);
        return okJson(callerVo);
    }

    /**
     * 调用方注册.
     */
    @PostMapping(value = "")
    public ApiResult<Integer> createCaller(CallerVo callerVo) {

        checkArgument(!StringUtil.isEmpty(callerVo.getCallername()), "调用者名称不能为空");

        log.info("createCaller req:{}", callerVo.toString());
        Integer createRet = callerService.createSelective(callerVo);
        Caller caller = callerDao.selectByName(callerVo.getCallername());
        operateRecordService.addOperateRecord(caller.getId(), null, OperateRecordType.CALLER_REGISTER, "注册调用方：" + caller);
        log.info("createCaller resp:{}", createRet);
        return okJson(createRet);
    }

    /**
     * 删除调用方.
     */
    @PostMapping(value = "/auth/del")
    public ApiResult<Caller> delCallerById(@RequestParam("cid") Integer id) {

        log.info("deleteCallerById req:{} ", id);

        Caller caller = callerDao.selectByPrimaryKey(id);
        Integer result = callerService.deleteByPrimaryKey(id);
        log.info("deleteCallerById resp:{}", result);
        if (result == SUCCESS.code()) {
            return okJson(id);
        }
        if (result == CONDITION_ILLEGAL.code()) {
            return errorJson(CONDITION_ILLEGAL.code(), "该调用方存在调用关系，请删除调用关系后重试");
        }
        operateRecordService.addOperateRecord( caller.getId(), null, OperateRecordType.CALLER_DELETE, "删除调用方：" + caller);
        BaseError error = BaseError.codeof(result);
        return errorJson(error.code(), error.getMessage());
    }

    /**
     * 更新调用方.
     */
    @PostMapping(value = "/auth/update")
    public ApiResult<Integer> updateCaller( CallerVo callerVo) {


        Integer id = callerVo.getId();

        checkArgument(null != id && 0 != id, "调用方id不能为空");
        checkArgument(checkCallerName(callerVo), "不允许修改调用者名称");

        log.info(" updateCallerByIdSelective req:{}, ",  id);
        Caller beforeCaller = callerDao.selectByPrimaryKey(id);
        Integer result = callerService.updateByPrimaryKeySelective(callerVo);
        Caller afterCaller = callerDao.selectByPrimaryKey(id);
        operateRecordService.addOperateRecord( beforeCaller.getId(), 0, OperateRecordType.CALLER_UPDATE, "更新调用方：" + DiffUtil.diffObjectField(beforeCaller, afterCaller, Stream.of("updateTime").collect(Collectors.toSet())));
        log.info("updateCallerByIdSelective resp:{}", result);
        if (result == 1) {
            return okJson(result);
        }
        return errorJson(ID_NOT_EXIST.code(), ID_NOT_EXIST.getMessage());
    }

    /**
     * 不允许更新调用方名称.
     */
    private boolean checkCallerName(CallerVo callerVo) {
        String callerName = callerVo.getCallername();
        if (StringUtil.isEmpty(callerName)) {
            return true;
        }
        CallerVo vo = callerService.findByPrimaryKey(callerVo.getId());
        return vo.getCallername().equals(callerName);
    }

    @GetMapping(value = "/list")
    public DataGrid<CallerVo> listCaller( Caller caller, @PageableDefault Pageable pageable) {
        log.info("op=start_listCaller,Caller={}",  caller);
        DataGrid<CallerVo> dataGrid = new DataGrid<CallerVo>();

        caller.setCallerKey(StringUtil.isEmpty(caller.getCallerKey()) ? null : caller.getCallerKey());
        List<CallerVo> callerVos = callerService.selectPage(caller, pageable);
        if (CollectionUtil.isEmpty(callerVos)) {
            dataGrid.setRows(new ArrayList<>());
            return dataGrid;
        }
        dataGrid.setCurrent(pageable.getPageNumber());
        dataGrid.setRowCount(pageable.getPageSize());
        dataGrid.setRows(callerVos);
        dataGrid.setTotal(callerService.selectCount(caller));
        return dataGrid;
    }

}
