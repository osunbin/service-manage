package com.bin.webmonitor.externapi;

import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.model.vo.CallerVo;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.service.CallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/extern/api/caller/")
public class CallerApi {

    private static final Logger logger = LoggerFactory.getLogger(CallerApi.class);


    @Autowired
    private CallerService callerService;

    @Autowired
    private CallerDao callerDao;


    @RequestMapping("/checkCallerName")
    public ApiResult<Boolean> checkCallerName(String callerName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String remoteIp) {
        try {
            logger.info("op=start_checkCallerName,callerName={},remoteIp={}", callerName, remoteIp);
            ApiResult<Boolean> res = validateCallerName(callerName);
            if (Objects.nonNull(res)) {
                return res.setResult(false);
            }
            Caller dbCaller = callerDao.selectByName(callerName);
            if (Objects.nonNull(dbCaller)) {
                return new ApiResult<Boolean>(1, "调用方名称已被占用！", callerName).setResult(false);
            }
            return new ApiResult<Boolean>().setCode(0).setResult(true);
        } catch (Exception e) {
            logger.error("[error_checkCallerName]callerName={},remoteIp={}", callerName, remoteIp, e);
            return new ApiResult<>(-1, "服务器错误！", "error", false);
        }
    }


    @RequestMapping("/registerCaller")
    public ApiResult<Caller> registerCaller(String callerName, String owners,  String description, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String remoteIp) {
        try {
            logger.info("op=start_registerCaller,callerName={},owners={},description={},remoteIp={}", callerName, owners,  description, remoteIp);
            CallerVo callerVo = new CallerVo.Builder().setCallerName(callerName).setOwners(owners).setDescription(description).builder();
            Integer cid = callerService.createSelective(callerVo);
            Caller registerCaller = callerDao.selectByPrimaryKey(cid);
            logger.info("op=end_registerCaller,caller={}", registerCaller);
            return new ApiResult<>(0, "创建成功!", "success", registerCaller);
        } catch (Exception e) {
            logger.error("[error_registerCaller]callerName={},owners={},description={},remoteIp={}", callerName, owners,  description, remoteIp, e);
            return new ApiResult<>(-1, "服务器错误！", "error", null);
        }
    }

    @RequestMapping("/getCallerByName")
    public ApiResult<Caller> getCallerByName(String callerName, @RequestHeader(required = false, name = "X-Forwarded-For", defaultValue = "127.0.0.1") String remoteIp) {
        try {
            logger.info("op=start_getCallerByName,callerName={},remoteIp={}", callerName, remoteIp);
            if (StringUtil.isEmpty(callerName)) {
                return new ApiResult<>(-1, "调用方名称为空", "error");
            }

            Caller caller = callerDao.selectByName(callerName);
            if (Objects.isNull(caller)) {
                return new ApiResult<>(-1, "调用方不存在", "error");
            }
            logger.info("op=end_getCallerByName,caller={}", caller);
            return new ApiResult<>(0, "", "success", caller);
        } catch (Exception e) {
            logger.error("[error_getCallerByName]callerName={},remoteIp={}", callerName, remoteIp, e);
            return new ApiResult<>(-1, "服务器错误！", "error", null);
        }
    }




    private static final String REGEX_CALLER_NAME = "[A-Za-z]+[A-Za-z0-9]{0,99}";


    private static ApiResult<Boolean> validateCallerName(String callerName) {
        if (StringUtil.isEmpty(callerName)) {
            return new ApiResult<Boolean>().setCode(1).setMessage("调用方名称不能为空！");
        }

        if (callerName.length() >= 100) {
            return new ApiResult<Boolean>().setCode(1).setMessage("调用方名称长度不能大于100！").setField(callerName);
        }

        if (!callerName.matches(REGEX_CALLER_NAME)) {
            return new ApiResult<Boolean>().setCode(1).setMessage("调用方名称只能由字母、数字组成，且首字符必须是字母！").setField(callerName);
        }
        return null;
    }
}
