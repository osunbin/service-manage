package com.bin.webmonitor.controller;

import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.model.vo.OperateRecordVo;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.OperateRecord;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.bin.webmonitor.service.OperateRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operateRecord")
public class OperateRecordController {
    private static Logger logger = LoggerFactory.getLogger(OperateRecordController.class);

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private LocalService localService;

    @Autowired
    private CallerDao callerDao;



    @RequestMapping("/recordList")
    public DataGrid<OperateRecordVo> getOperateRecord( @RequestParam(value = "serviceName", required = false) String serviceName, @RequestParam(value = "callerName", required = false) String callerName) {
        logger.info("op=start_getOperateRecord,serviceName={}",  serviceName);
        Integer sid = null;
        Integer cid = null;
        if (!StringUtil.isEmpty(serviceName)) {
            ServiceInstance service = localService.getByName(serviceName);
            sid = Objects.nonNull(service) ? service.getId() : null;
        }
        if (!StringUtil.isEmpty(callerName)) {
            Caller caller = callerDao.selectByName(callerName);
            cid = Objects.nonNull(caller) ? caller.getId() : null;
        }
        List<OperateRecord> operateRecordPos = operateRecordService.getOperateRecordByCidAndSid(cid, sid);
        List<OperateRecordVo> res = operateRecordPos.stream().map(this::operateRecordPo2Vo).collect(Collectors.toList());

        DataGrid<OperateRecordVo> dataGrid = new DataGrid<>();
        dataGrid.setRows(res).setTotal(res.size()).setCurrent(1);
        return dataGrid;
    }

    private OperateRecordVo operateRecordPo2Vo(OperateRecord operateRecordPo) {
        OperateRecordVo res = new OperateRecordVo();
        res.setUid(operateRecordPo.getUid());
        res.setOpType(OperateRecordType.codeOf(operateRecordPo.getOpType()).getDesc());
        res.setContent(operateRecordPo.getContent());

        res.setCreateTime(TimeUtil.date2fullStr(operateRecordPo.getCreateTime()));
        res.setUpdateTime(TimeUtil.date2fullStr(operateRecordPo.getUpdateTime()));
        return res;
    }
}
