package com.bin.webmonitor.service;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.component.SendCommand;
import com.bin.webmonitor.model.dto.FuncConfigDto;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CallerFuncUsageService {

    private static final Logger logger = LoggerFactory.getLogger(CallerFuncUsageService.class);

    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;
    @Autowired
    private ServiceFunctionDao serviceFunctionDao;
    @Autowired
    private SendCommand sendCommand;



    public List<FuncConfigDto> listBySid(Integer sid, Integer cid) {
        List<ServiceFunction> functions = serviceFunctionDao.getServiceFunctionsBySid(sid);
        if (0 == cid) {
            return getAllServiceFunc(functions);
        }
        List<CallerFunctionUseage> poList = getCallerFuncUsageList(sid, cid);
        return poList.stream().map((Function<CallerFunctionUseage, FuncConfigDto>) input -> {
            FuncConfigDto dto = new FuncConfigDto();
            dto.setCallFuncUsageId(input.getId());
            dto.setCallFuncUsageId(input.getId());
            dto.setFuncName(input.getFname());
            dto.setQuantity(String.valueOf(input.getQuantity()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void degradeFunctions(Integer cid, Integer sid, String fids) {
        List<Integer> fidList = DelimiterHelper.splitToList(fids)
                .stream().map(Integer::parseInt).collect(Collectors.toList());
        CallerFunctionUseage po = new CallerFunctionUseage();
        po.setCid(cid);
        po.setSid(sid);
        List<CallerFunctionUseage> poList = selectPage(po, Constants.PAGE_MAX);

        for (CallerFunctionUseage funcUsagePo : poList) {
            Integer fid = funcUsagePo.getFid();
            if (fidList.contains(fid)) {
                funcUsagePo.setDegrade(true);
            } else {
                funcUsagePo.setDegrade(false);
            }
            callerFuncUsageDao.batchUpdate(poList);
        }
        sendCommand.sendDegradeCommand(sid, cid);
    }

    @Transactional(rollbackFor = Exception.class)
    public String delBatch(Integer cid, Integer sid, String ids) {
        List<String> idList = DelimiterHelper.splitToList(ids);
        List<CallerFunctionUseage> callerFuncUsageList = getCallerFuncUsageList(cid, sid);
        Set<Integer> originIdSet = callerFuncUsageList.stream().map(
                CallerFunctionUseage::getId).collect(Collectors.toSet());
        List<String> delFailIds = new ArrayList<>();
        for (String idStr : idList) {
            try {
                String delId = delById(originIdSet, idStr);
                if (DelimiterHelper.notEmpty(delId)) {
                    delFailIds.add(delId);
                }
            } catch (Exception e) {
                logger.warn("delBatch error:{}", e.getMessage(), e);
                delFailIds.add(idStr);
            }
        }
        return DelimiterHelper.join(delFailIds);
    }

    
    public String delAll(Integer cid, Integer sid) {
        List<String> delFailIds = new ArrayList<>();
        List<CallerFunctionUseage> callerFuncUsageList = getCallerFuncUsageList(cid, sid);
        for (CallerFunctionUseage po : callerFuncUsageList) {
            Integer id = po.getId();
            int ret = deleteByPrimaryKey(id);
            if (ret == 0) {
                logger.info("id:{} del fail", id);
                delFailIds.add(String.valueOf(id));
            }
        }
        return DelimiterHelper.join(delFailIds);
    }

    private String delById(Set<Integer> originIdSet, String idStr) {
        Integer id = Integer.parseInt(idStr);
        if (originIdSet.contains(id)) {
            Integer result = deleteByPrimaryKey(id);
            if (result == 0) {
                logger.info("id:{} del fail", idStr);
                return idStr;
            }
            return "";
        } else {
            logger.info("id:{} not in originId list", idStr);
            return idStr;
        }
    }

    
    public List<CallerFunctionUseage> getCallerFuncUsageList(Integer cid, Integer sid) {
        CallerFunctionUseage po = new CallerFunctionUseage();
        po.setCid(cid);
        po.setSid(sid);
        return selectPage(po, Constants.PAGE_MAX);
    }

    private List<FuncConfigDto> getAllServiceFunc(List<ServiceFunction> functions) {
        return functions.stream().map((Function<ServiceFunction, FuncConfigDto>) input -> {
            FuncConfigDto dto = new FuncConfigDto();
            dto.setFuncName(input.getFname());
            dto.setQuantity("0");
            return dto;
        }).collect(Collectors.toList());
    }


    public int create(CallerFunctionUseage callerFuncUsagePo) {
        return callerFuncUsageDao.insert(callerFuncUsagePo);
    }


    public int createSelective(CallerFunctionUseage callerFuncUsagePo) {
        return callerFuncUsageDao.insertSelective(callerFuncUsagePo);
    }


    public int deleteByPrimaryKey(Integer id) {
        return callerFuncUsageDao.deleteByPrimaryKey(id);
    }


    public CallerFunctionUseage findByPrimaryKey(Integer id) {
        return callerFuncUsageDao.selectByPrimaryKey(id);
    }


    public int selectCount(CallerFunctionUseage callerFuncUsagePo) {
        return callerFuncUsageDao.selectCount(callerFuncUsagePo);
    }


    public int updateByPrimaryKey(CallerFunctionUseage callerFuncUsagePo) {
        return callerFuncUsageDao.updateByPrimaryKey(callerFuncUsagePo);
    }


    public int updateByPrimaryKeySelective(CallerFunctionUseage callerFuncUsagePo) {
        return callerFuncUsageDao.updateByPrimaryKeySelective(callerFuncUsagePo);
    }

    public List<CallerFunctionUseage> selectPage(CallerFunctionUseage callerFuncUsagePo,
                                                 Pageable pageable) {
        List<CallerFunctionUseage> callerFuncUsagePos = callerFuncUsageDao.selectPage(
                callerFuncUsagePo, pageable);
        if (CollectionUtil.isEmpty(callerFuncUsagePos)) {
            return Collections.emptyList();
        }
        return callerFuncUsagePos;
    }


    public int updateTimeoutByCidSidFid(Integer cid, Integer sid, Integer fid, Integer funcTimeout) {
        CallerFunctionUseage queryCallerFunctionUseage = new CallerFunctionUseage();
        queryCallerFunctionUseage.setCid(cid);
        queryCallerFunctionUseage.setSid(sid);
        queryCallerFunctionUseage.setFid(fid);

        List<CallerFunctionUseage> callerFuncUsagePos=selectPage(queryCallerFunctionUseage,Constants.PAGE_MAX);

        if(CollectionUtil.isEmpty(callerFuncUsagePos)) {
            return 0;
        }

        CallerFunctionUseage callerFuncUsagePo=callerFuncUsagePos.get(0);
        callerFuncUsagePo.setTimeout(funcTimeout);
        return callerFuncUsageDao.updateByPrimaryKeySelective(callerFuncUsagePo);
    }

    
    public List<CallerFunctionUseage> getExistTimeoutFunctionUsages(int cid, int sid) {
        CallerFunctionUseage po = new CallerFunctionUseage();
        po.setCid(cid);
        po.setSid(sid);
        List<CallerFunctionUseage> res = selectPage(po, Constants.PAGE_MAX).stream()
                .filter(callerFuncUsagePo -> callerFuncUsagePo.getTimeout() != 0).collect(Collectors.toList());
        return res;
    }

    
    public List<String> getNoTimeoutFunctionNames(int cid, int sid) {

        Set<Integer> timeoutFids = getExistTimeoutFunctionUsages(cid, sid).stream().map(CallerFunctionUseage::getFid).collect(Collectors.toSet());

        Set<Integer> allFids = serviceFunctionDao.getServiceFunctionsBySid(sid).stream().map(ServiceFunction::getId).collect(Collectors.toSet());
        allFids.removeAll(timeoutFids);

        String noTimeoutFids = DelimiterHelper.join(allFids);
        List<String> res = serviceFunctionDao.multiGet(noTimeoutFids)
                .stream().map(ServiceFunction::getFname).collect(Collectors.toList());
        logger.info("op=end_listNoTimeoutFunction,cid={},sid={},res={}", cid, sid, res);
        return res;
    }
}
