package com.bin.webmonitor.service;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.repository.dao.OperateRecordDao;
import com.bin.webmonitor.repository.domain.OperateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OperateRecordService {
    private static Logger logger = LoggerFactory.getLogger(OperateRecordService.class);

    @Autowired
    private OperateRecordDao OperateRecordDao;


    public void addOperateRecord(Integer cid, Integer sid, OperateRecordType type, String content) {
        addOperateRecord("sysUser",cid,sid,type,content);
    }

    public void addOperateRecord(String uid, Integer cid, Integer sid, OperateRecordType type, String content) {
        logger.info("op=start_addOperateRecord,uid={},cid={},sid={},content={}", uid, cid, sid, content);
        uid = StringUtil.isEmpty(uid) ? "" : uid;
        OperateRecord operateRecordPo = new OperateRecord.Builder()
                .setuid(uid)
                .setCid(Objects.isNull(cid) ? 0 : cid)
                .setSid(Objects.isNull(sid) ? 0 : sid)
                .setOpType(type.getCode())
                .setContent(content)
                .setCreateTime(new Date())
                .builder();
        OperateRecordDao.insert(operateRecordPo);
    }


    public List<OperateRecord> getOperateRecordByCidAndSid(Integer cid, Integer sid) {
        OperateRecord operateRecordPo = new OperateRecord.Builder().setSid(sid).setCid(cid).builder();
        return OperateRecordDao.selectPage(operateRecordPo, Constants.PAGE_MAX);
    }
}
