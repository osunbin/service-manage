package com.bin.webmonitor.service;

import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.common.util.DesHelper;
import com.bin.webmonitor.common.util.TimeUtil;
import com.bin.webmonitor.model.vo.CallerVo;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerUseage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.bin.webmonitor.common.BaseError.CONDITION_ILLEGAL;
import static com.bin.webmonitor.common.BaseError.ID_NOT_EXIST;
import static com.bin.webmonitor.common.BaseError.SUCCESS;
import static com.bin.webmonitor.common.Constants.PAGE_MAX;

@Service
public class CallerService {

    private final Logger log = LoggerFactory.getLogger(CallerService.class);

    @Autowired
    private CallerDao callerDao;



    @Autowired
    private CallerUsageService callerUsageService;
    /**
     * 调用者KEY的加密盐.
     */
    private static final String KEY_SALT = "platform_@";

    /**
     * 调用者KEY的版本号.
     */
    private static final String KEY_VERSION = "1.0.0";


    /**
     * 根据调用者ID生成KEY.
     */
    private String genCallerKey(Integer callerId) throws Exception {
        String key = KEY_VERSION + "_" + callerId;
        return DesHelper.encryptString(key, KEY_SALT);
    }


    public Boolean isCallerOwner(Integer cid) {
        Caller caller = callerDao.selectByPrimaryKey(cid);
        if (null == caller) {
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Integer id) {
        if (hasDependency(id)) {
            return CONDITION_ILLEGAL.code();
        }


        int delRet = callerDao.deleteByPrimaryKey(id);
        if (delRet > 0) {
            return SUCCESS.code();
        }
        return ID_NOT_EXIST.code();
    }




    private boolean hasDependency(Integer id) {
        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setCid(id);
        int count = callerUsageService.selectCount(callerUseage);
        return count > 0;
    }

    public int create(Caller caller) {
        return callerDao.insert(caller);
    }

    @Transactional(rollbackFor = Exception.class)
    public int createSelective(CallerVo callerVo) {
        Caller caller = new Caller();
        BeanUtils.copyProperties(callerVo, caller);
        callerDao.insertSelective(caller);
        try {
            Integer poId = caller.getId();
            String callerKey = genCallerKey(poId);
            caller.setCallerKey(callerKey);
            int updateRet = callerDao.updateByPrimaryKeySelective(caller);
            log.info("updateRet :{}", updateRet);
            return poId;
        } catch (Exception e) {
            log.warn("gen callerKey error:{}", e.getMessage(), e);
            throw new RuntimeException("gen callerKey error");
        }
    }


    public CallerVo findByPrimaryKey(Integer id) {
        Caller caller = callerDao.selectByPrimaryKey(id);
        CallerVo callerVo = new CallerVo();
        BeanUtils.copyProperties(caller, callerVo);
        return callerVo;
    }


    public int selectCount(Caller caller) {
        return callerDao.selectCount(caller);
    }


    public int updateByPrimaryKey(Caller caller) {
        return callerDao.updateByPrimaryKey(caller);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateByPrimaryKeySelective(CallerVo callerVo) {
        Caller caller = new Caller();
        BeanUtils.copyProperties(callerVo, caller);
        return callerDao.updateByPrimaryKeySelective(caller);
    }



    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<CallerVo> selectPage(Caller caller, Pageable pageable) {
        List<Caller> callers = callerDao.selectPage(caller, pageable);


        return callers.stream().map(po -> {
            CallerVo callerVo = new CallerVo();
            BeanUtils.copyProperties(po, callerVo);
            callerVo.setCreatetimeStr(TimeUtil.date2fullStr(callerVo.getCreatetime()));
            return callerVo;
        }).collect(Collectors.toList());
    }

    


    
    public String getCallerKey(Integer id) {
        Caller caller = callerDao.selectByPrimaryKey(id);
        return caller.getCallerKey();
    }

}
