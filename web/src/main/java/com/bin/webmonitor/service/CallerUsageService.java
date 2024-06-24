package com.bin.webmonitor.service;

import com.bin.webmonitor.command.ConfigChangeCommand;
import com.bin.webmonitor.command.RejectCommand;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.DiffUtil;
import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.component.SendCommand;
import com.bin.webmonitor.enums.OperateRecordType;
import com.bin.webmonitor.model.dto.FuncConfigDto;
import com.bin.webmonitor.model.dto.FunctionTimeoutDto;
import com.bin.webmonitor.model.dto.CallerConfigDto;
import com.bin.webmonitor.model.vo.CallerConfigVo;
import com.bin.webmonitor.model.vo.CallerFuncUsageVo;
import com.bin.webmonitor.model.vo.CallerUsageVo;
import com.bin.webmonitor.model.vo.IdNameVo;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerDao;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.CallerSpecialDao;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.ServiceDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.CallerSpecialAttr;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bin.webmonitor.common.Constants.PAGE_MAX;
import static com.bin.webmonitor.common.Constants.PAGE_ONE;

@Service
public class CallerUsageService {

    private static Logger log = LoggerFactory.getLogger(CallerUsageService.class);


    @Autowired
    private CallerUsageDao callerUsageDao;

    @Autowired
    private CallerDao callerDao;

    @Autowired
    private ServiceDao serviceDao;



    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;

    @Autowired
    private CallerSpecialDao callerSpecialDao;

    @Autowired
    private ServiceFunctionDao serviceFunctionDao;


    @Autowired
    private LocalService localService;
    @Autowired
    private LocalCaller localCaller;
    @Autowired
    private CallerFuncUsageService callerFuncUsageService;
    @Autowired
    private SendCommand sendCommand;


    @Autowired
    private OperateRecordService operateRecordService;

    public int create(CallerUseage callerUseage) {
        return callerUsageDao.insert(callerUseage);
    }


    public int createSelective(CallerUseage callerUseage) {
        return callerUsageDao.insertSelective(callerUseage);
    }


    public int deleteByPrimaryKey(Integer id) {
        return callerUsageDao.deleteByPrimaryKey(id);
    }


    public CallerUseage findByPrimaryKey(Integer id) {
        return callerUsageDao.selectByPrimaryKey(id);
    }


    public int selectCount(CallerUseage callerUseage) {
        return callerUsageDao.selectCount(callerUseage);
    }



    public int updateByPrimaryKey(CallerUseage callerUseage) {
        return callerUsageDao.updateByPrimaryKey(callerUseage);
    }


    public int updateByPrimaryKeySelective(CallerUseage callerUseage) {
        return callerUsageDao.updateByPrimaryKeySelective(callerUseage);
    }


    public List<CallerUseage> selectPage(CallerUseage callerUseage, Pageable pageable) {
        List<CallerUseage> callerUsagePos = callerUsageDao.selectPage(callerUseage, pageable);
        if (CollectionUtil.isEmpty(callerUsagePos)) {
            return Collections.emptyList();
        }
        return callerUsagePos;
    }


    public Page<CallerUsageVo> selectVoPage(CallerUseage callerUseage, Pageable pageable) {

        List<CallerUseage> callerUsagePos = selectPage(callerUseage, pageable);
        ;

        List<CallerUsageVo> voList = callerUsagePos.stream().map(po -> {
            CallerUsageVo callerUsageVo = new CallerUsageVo();
            BeanUtils.copyProperties(po, callerUsageVo);
            ServiceInstance serviceById = serviceDao.selectById(po.getSid());
            String serviceName = serviceById.getServiceName();
            callerUsageVo.setSvcName(serviceName);
            return callerUsageVo;
        }).collect(Collectors.toList());
        return new PageImpl<>(voList, pageable, selectCount(callerUseage));
    }

    public CallerConfigVo genCallerConfig(Integer cid, Integer sid) {
        List<CallerUseage> callerUsagePoList = getCallerUsagePos(cid, sid);
        if (CollectionUtil.isEmpty(callerUsagePoList)) {
            return new CallerConfigVo();
        }
        CallerUseage callerUseage = callerUsagePoList.get(0);
        List<CallerFuncUsageVo> vos = getFuncUsageVoList(cid, sid);

        CallerConfigVo vo = new CallerConfigVo();
        vo.setCallerName(callerDao.selectByPrimaryKey(cid).getCallerName());
        vo.setSvcName(localService.getById(sid).getServiceName());
        BeanUtils.copyProperties(callerUseage, vo);
        vo.setFuncUsageVoList(vos);

        Set<Integer> alreadyApplyFids = vos.stream().map(CallerFuncUsageVo::getFid).collect(Collectors.toSet());
        List<ServiceFunction> functions = serviceFunctionDao.getServiceFunctionsBySid(sid);
        List<ServiceFunction> notApplyFuns = new ArrayList<>();
        if (!CollectionUtil.isEmpty(functions)) {
            notApplyFuns = functions.stream().filter(i -> !alreadyApplyFids.contains(i.getId())).collect(Collectors.toList());
        }
        vo.setNotApply(notApplyFuns);
        return vo;
    }


    public List<CallerUseage> getCallerUsageList(Integer cid, Integer sid) {
        CallerUseage po = new CallerUseage();
        po.setCid(cid);
        po.setSid(sid);
        return selectPage(po, PAGE_MAX);
    }


    public List<IdNameVo> getCallerNames(Integer sid) {
        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setSid(sid);
        List<CallerUseage> callerUsagePos = selectPage(callerUseage, PAGE_MAX);
        return callerUsagePos.stream().map(po -> {
            Integer cid = po.getCid();
            Caller caller = callerDao.selectByPrimaryKey(cid);
            if (null != caller) {
                return new IdNameVo(caller.getId(), caller.getCallerName());
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    public List<IdNameVo> getSvcNames(Integer cid) {
        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setCid(cid);
        List<CallerUseage> callerUsagePos = selectPage(callerUseage, PAGE_MAX);
        return callerUsagePos.stream().map(po -> {
            Integer sid = po.getSid();
            ServiceInstance serviceById = localService.getById(sid);
            if (null != serviceById) {
                return new IdNameVo(serviceById.getId(), serviceById.getServiceName());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<CallerUseage> getCallerUsagePos(Integer cid, Integer sid) {
        CallerUseage po = new CallerUseage();
        po.setCid(cid);
        po.setSid(sid);
        return selectPage(po, PAGE_ONE);
    }

    private List<CallerFuncUsageVo> getFuncUsageVoList(Integer cid, Integer sid) {
        CallerFunctionUseage callerFuncUsagePo = new CallerFunctionUseage();
        callerFuncUsagePo.setCid(cid);
        callerFuncUsagePo.setSid(sid);
        List<CallerFunctionUseage> callerFuncUsagePoList = callerFuncUsageService.selectPage(callerFuncUsagePo, PAGE_MAX);

        return callerFuncUsagePoList.stream().map(item -> {
            CallerFuncUsageVo vo = new CallerFuncUsageVo();
            vo.setFuncName(item.getFname());
            vo.setQuantity(item.getQuantity());
            vo.setFid(item.getFid());
            return vo;
        }).collect(Collectors.toList());
    }


    public boolean isExist(Integer cid, Integer sid) {
        CallerUseage po = new CallerUseage();
        po.setCid(cid);
        po.setSid(sid);
        int count = selectCount(po);
        return count > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteCallerUsage(Integer id, Integer cid, Integer sid) {
        delFuncUsages(cid, sid);
        delSpecials(cid, sid);
        return callerUsageDao.deleteByPrimaryKey(id);
    }



    private void delSpecials(Integer cid, Integer sid) {
        CallerSpecialAttr callerSpecialAttr = new CallerSpecialAttr();
        callerSpecialAttr.setCid(cid);
        callerSpecialAttr.setSid(sid);
        List<CallerSpecialAttr> callerSpecialAttrs = callerSpecialDao.selectPage(callerSpecialAttr, PAGE_MAX);
        for (CallerSpecialAttr specialPo : callerSpecialAttrs) {
            callerSpecialDao.deleteByPrimaryKey(specialPo.getId());
        }
    }

    private void delFuncUsages(Integer cid, Integer sid) {
        CallerFunctionUseage funcUsagePo = new CallerFunctionUseage();
        funcUsagePo.setSid(sid);
        funcUsagePo.setCid(cid);
        List<CallerFunctionUseage> callerFunctionUseages = callerFuncUsageDao.selectPage(funcUsagePo, PAGE_MAX);
        for (CallerFunctionUseage po : callerFunctionUseages) {
            callerFuncUsageDao.deleteByPrimaryKey(po.getId());
        }
    }


    // TODO 
    public void updateCaller(CallerConfigDto callerConfigDto) {
        log.info("op=start_updateCaller,callerConfigDto={}", callerConfigDto);
        Integer cid = 1;
        Integer sid = 1;
        ServiceInstance service = localService.getById(sid);
        Caller caller = localCaller.getById(cid);

        CallerUseage callerUseage = new CallerUseage();
        callerUseage.setCid(cid);
        callerUseage.setSid(sid);
        CallerUseage originCallerUsage = callerUsageDao.selectByCidSid(cid, sid);
        if (Objects.isNull(originCallerUsage)) {
            log.error("[ERROR_not_exists_callerUsage]callerConfigDto={}", callerConfigDto);
            return;
        }

        CallerUseage newCallerUsage = new CallerUseage();
        BeanUtils.copyProperties(callerConfigDto, newCallerUsage);
        if (originCallerUsage.equals(newCallerUsage)) { // 判断调用关系配置是否有更新
            log.info("[no_callerUsage_config_change]callerConfigDto={},originCallerUsage={},newCallerUsage={}", newCallerUsage, originCallerUsage, newCallerUsage);
            return;
        }

        // 新配置更新到DB
        newCallerUsage.setId(originCallerUsage.getId());
        callerUsageDao.updateByPrimaryKeySelective(newCallerUsage);
        log.info("[updateCaller_save_newCallerUsage]callerConfigDto={},originCallerUsage={},newCallerUsage={}", newCallerUsage, originCallerUsage, newCallerUsage);

        // 如果是服务级粒度，则要删除之前函数级的申请
        if (newCallerUsage.getGranularity() == Constants.USAGE_GRANULARITY_SERVER) {
            String delResult = callerFuncUsageService.delAll(cid, sid);
            log.info("op=end_del_all_func_usage,cid={},sid={},delResult={}", cid, sid, delResult);
        }

        // 如果是函数级粒度
        if (newCallerUsage.getGranularity() == Constants.USAGE_GRANULARITY_FUNCTION) {
            CallerFunctionUseage po = new CallerFunctionUseage();
            po.setCid(cid);
            po.setSid(sid);
            List<FuncConfigDto> funcConfigDtoList = JsonHelper.strToList(callerConfigDto.getFuncConfigsJson(), FuncConfigDto.class);

            List<CallerFunctionUseage> originPoList = callerFuncUsageService.selectPage(po, Constants.PAGE_MAX);
            updateFuncUsage(cid, sid, funcConfigDtoList, originPoList);
        }

        // 配置函数超时
        if (StringUtil.isNotEmpty(callerConfigDto.getFunctionTimeoutJson())) {
            List<FunctionTimeoutDto> functionTimeoutDtos = JsonHelper.strToList(callerConfigDto.getFunctionTimeoutJson(), FunctionTimeoutDto.class);
            setFuncTimeout(cid, sid, functionTimeoutDtos);
            log.info("op=end_set_function_timeout,detail={}", callerConfigDto.getFunctionTimeoutJson());
        }

        // 给调用方和服务方发送配置变更通知
        sendCommand.sendClient(caller.getCallerKey(), new ConfigChangeCommand().setService(service.getServiceName()));
        sendCommand.sendService(service.getServiceName(), new RejectCommand().setCallerKey(caller.getCallerKey()));

        recodeUpdateCallerUsageFinish(originCallerUsage, callerConfigDto, service, caller);
        log.info("op=end_updateCaller,callerConfigDto={}", callerConfigDto);
    }

    /**
     * 批量修改可能存在新增及删除
     */
    private void updateFuncUsage(Integer cid, Integer sid, List<FuncConfigDto> nowConfigList, List<CallerFunctionUseage> originList) {

        Map<String, CallerFunctionUseage> callerFuncUsageMap = originList.stream().collect(Collectors.toMap(CallerFunctionUseage::getFname, po -> po));
        Set<String> configSet = nowConfigList.stream().map(FuncConfigDto::getFuncName).collect(Collectors.toSet());
        Set<String> originFuncNameSet = callerFuncUsageMap.keySet();

        Sets.SetView<String> createSet = Sets.difference(configSet, originFuncNameSet);
        Sets.SetView<String> updateSet = Sets.intersection(originFuncNameSet, configSet);
        Sets.SetView<String> delSet = Sets.difference(originFuncNameSet, configSet);

        processConfigList(cid, sid, nowConfigList, callerFuncUsageMap, createSet, updateSet, delSet);
    }

    private boolean processConfigList(Integer cid, Integer sid, List<FuncConfigDto> nowConfigList, Map<String, CallerFunctionUseage> callerFuncUsageMap, Sets.SetView<String> createSet, Sets.SetView<String> updateSet, Sets.SetView<String> delSet) {

        boolean addOrDeleteService = false;
        for (FuncConfigDto dto : nowConfigList) {
            String quantity = StringUtil.defaultIfEmpty(dto.getQuantity(), "0");
            Integer quantityInt = Integer.parseInt(quantity);
            String funcName = dto.getFuncName();
            CallerFunctionUseage nowCallerFuncUsage = callerFuncUsageMap.get(funcName);
            if (null == nowCallerFuncUsage) {
                List<ServiceFunction> serviceFunctions = serviceFunctionDao.getServiceFunctionsBySid(sid);
                for (ServiceFunction sf : serviceFunctions) {
                    if (funcName.equals(sf.getFname()) && createSet.contains(funcName)) {
                        addOrDeleteService = processCreateSet(cid, sid, addOrDeleteService, quantityInt, funcName, sf.getId());
                    }
                }
                continue;
            }
            Integer fid = nowCallerFuncUsage.getFid();
            if (createSet.contains(funcName)) {
                addOrDeleteService = processCreateSet(cid, sid, addOrDeleteService, quantityInt, funcName, fid);
            }
            if (updateSet.contains(funcName)) {
                processUpdateSet(cid, sid, quantityInt, funcName, nowCallerFuncUsage, fid);
            }
            if (delSet.contains(funcName)) {
                addOrDeleteService = processDelSet(cid, funcName, nowCallerFuncUsage, fid);
            }
        }
        for (String delFuncName : delSet) {
            CallerFunctionUseage nowCallerFuncUsage = callerFuncUsageMap.get(delFuncName);
            Integer fid = nowCallerFuncUsage.getFid();
            addOrDeleteService = processDelSet(cid, delFuncName, nowCallerFuncUsage, fid);
        }
        return addOrDeleteService;
    }

    private boolean processDelSet(Integer cid, String funcName, CallerFunctionUseage funcUsagePo, Integer fid) {
        log.info("op=start_processDelSet,cid={},funcName={},funcUsagePo={},fid={}", cid, funcName, funcUsagePo, fid);

        callerFuncUsageService.deleteByPrimaryKey(funcUsagePo.getId());
        return true;
    }

    private void processUpdateSet(Integer cid, Integer sid, Integer quantityInt, String funcName, CallerFunctionUseage funcUsagePo, Integer fid) {
        log.info("op=start_processUpdateSet,cid={},sid={},quantityInt={},funcName={},funcUsagePo={},fid={}", cid, sid, quantityInt, funcName, funcUsagePo, fid);
        Integer originQuantity = funcUsagePo.getQuantity();
        if (originQuantity.equals(quantityInt)) {
            return;
        }
        CallerFunctionUseage callerFuncUsagePo = newCaller(cid, sid, quantityInt, funcName, fid);
        if (null != callerFuncUsagePo) {
            Integer quantity = callerFuncUsagePo.getQuantity();
            Integer id = funcUsagePo.getId();
            callerFuncUsagePo.setId(id);
            callerFuncUsagePo.setQuantity(quantity);
            callerFuncUsageService.updateByPrimaryKeySelective(callerFuncUsagePo);
        }
    }


    private boolean processCreateSet(Integer cid, Integer sid, boolean addOrDeleteService, Integer quantityInt, String funcName, Integer fid) {
        log.info("op=start_processCreateSet,cid={},sid={},addOrDeleteService={},quantityInt={},funcName={},fid={}", cid, sid, addOrDeleteService, quantityInt, funcName, fid);
        CallerFunctionUseage callerFuncUsagePo = newCaller(cid, sid, quantityInt, funcName, fid);
        if (null == callerFuncUsagePo) {
            return addOrDeleteService;
        }

        callerFuncUsageService.createSelective(callerFuncUsagePo);
        return true;
    }

    private CallerFunctionUseage newCaller(Integer cid, Integer sid, Integer quantityInt, String funcName, Integer fid) {
        CallerFunctionUseage po = new CallerFunctionUseage();
        if (0 != fid) {
            po.setFid(fid);
            po.setCid(cid);
            po.setSid(sid);
            po.setFname(funcName);
            po.setQuantity(quantityInt);
            return po;
        }
        return null;
    }

    /**
     * 设置函数超时时间
     * @param cid 调用方
     * @param sid 服务方
     * @param functionTimeoutDtos 函数超时列表，其中为函数名和超时时间
     */
    private void setFuncTimeout(int cid, int sid, List<FunctionTimeoutDto> functionTimeoutDtos) {

        // 1.获取待更新的函数id与超时时间映射
        Map<Integer, Integer> fid2TimeoutMap = getfid2TimeoutMap(sid, functionTimeoutDtos);
        Set<Integer> newFids = fid2TimeoutMap.keySet();

        // 2.获取已设置函数超时的函数id
        Set<Integer> oldFids = callerFuncUsageService.getExistTimeoutFunctionUsages(cid, sid).stream().map(CallerFunctionUseage::getFid).collect(Collectors.toSet());

        // 3，分别获取新设置超时的函数，修改超时的删除和删除超时的函数
        Set<Integer> newFunction = Sets.difference(newFids, oldFids);
        Set<Integer> updateTimoutFunction = Sets.intersection(newFids, oldFids);
        Set<Integer> deleteFunction = Sets.difference(oldFids, newFids);
        log.info("op=setFuncTimeout,newfids={},updateFids={},delFids={}", newFunction, updateTimoutFunction, deleteFunction);

        newFunction.forEach(newFid -> setNewTimeout(cid, sid, newFid, fid2TimeoutMap.get(newFid)));
        updateTimoutFunction.forEach(updateFid -> callerFuncUsageService.updateTimeoutByCidSidFid(cid, sid, updateFid, fid2TimeoutMap.get(updateFid)));
        deleteFunction.forEach(delFid -> callerFuncUsageService.updateTimeoutByCidSidFid(cid, sid, delFid, 0));

        // 5.清除无用项目
        cleanCallerFunctionUsage(cid, sid);
    }

    /**
     * 获得函数id与超时时间的map
     *
     * @param sid 服务id
     * @param functionTimeoutDtos 超时列表
     * @return 函数id与超时时间的map
     */
    private Map<Integer, Integer> getfid2TimeoutMap(int sid, List<FunctionTimeoutDto> functionTimeoutDtos) {
        Map<Integer, Integer> res = new HashMap<>();
        for (FunctionTimeoutDto functionTimeoutDto : functionTimeoutDtos) {
            Integer fid = serviceFunctionDao.getServiceFunctionsBySidAndFname(sid, functionTimeoutDto.getFunctionName()).getId();
            int timeout = functionTimeoutDto.getFunctionTimout();
            if (timeout != 0) {
                res.put(fid, timeout);
            }
        }
        return res;
    }

    private void cleanCallerFunctionUsage(int cid, int sid) {
        CallerFunctionUseage requestPo = new CallerFunctionUseage();
        requestPo.setCid(cid);
        requestPo.setSid(sid);
        List<CallerFunctionUseage> callerFuncUsagePos = callerFuncUsageService.selectPage(requestPo, Constants.PAGE_MAX);
        for (CallerFunctionUseage callerFuncUsagePo : callerFuncUsagePos) {
            if (callerFuncUsagePo.getTimeout() == 0 && callerFuncUsagePo.getQuantity() == 0) {
                callerFuncUsageService.deleteByPrimaryKey(callerFuncUsagePo.getId());
                log.info("op=cleanCallerFunctionUsage,cid={},sid={},callerFuncUsagePo={}", cid, sid, callerFuncUsagePo);
            }
        }
    }

    /**
     *  设置新的超时时间
     * @param cid 调用方id
     * @param sid 服务方id
     * @param fid 函数id
     * @param timeout 超时时间
     */
    private void setNewTimeout(int cid, int sid, int fid, int timeout) {
        CallerFunctionUseage requestCallerFunctionUseage = new CallerFunctionUseage();
        requestCallerFunctionUseage.setCid(cid);
        requestCallerFunctionUseage.setSid(sid);
        requestCallerFunctionUseage.setFid(fid);
        List<CallerFunctionUseage> callerFuncUsagePos = callerFuncUsageService.selectPage(requestCallerFunctionUseage, Constants.PAGE_MAX);
        if (CollectionUtil.isEmpty(callerFuncUsagePos)) {
            String functionName = serviceFunctionDao.getServiceFunctionsByid(fid).getFname();
            CallerFunctionUseage callerFuncUsagePo = new CallerFunctionUseage();
            callerFuncUsagePo.setCid(cid);
            callerFuncUsagePo.setSid(sid);
            callerFuncUsagePo.setFid(fid);
            callerFuncUsagePo.setTimeout(timeout);
            callerFuncUsagePo.setCreateTime(new Date());
            callerFuncUsagePo.setQuantity(0);
            callerFuncUsagePo.setFname(functionName);
            callerFuncUsageService.createSelective(callerFuncUsagePo);
        } else {
            callerFuncUsageService.updateTimeoutByCidSidFid(cid, sid, fid, timeout);
        }
    }



    private void recodeUpdateCallerUsageFinish(CallerUseage originCallerUsage, CallerConfigDto callerConfigDto, ServiceInstance service, Caller caller) {
        try {
            CallerUseage afterCallerUsage = callerUsageDao.selectByCidSid(caller.getId(), service.getId());

            StringBuilder builder = new StringBuilder(String.format("申请修改调用关系%s->%s审核通过：%s", caller.getCallerName(), service.getServiceName(), DiffUtil.diffObjectField(originCallerUsage, afterCallerUsage, Stream.of("updateTime", "updatetime").collect(Collectors.toSet()))));
            if (callerConfigDto.getGranularity() == Constants.USAGE_GRANULARITY_FUNCTION) {
                builder.append("函数调用量：").append(DiffUtil.diffFunctionQuantity(callerConfigDto.getFuncConfigsJson()));
            }
            builder.append(DiffUtil.diffFunctionTimeout(callerConfigDto.getFunctionTimeoutJson()));
            operateRecordService.addOperateRecord( callerConfigDto.getCid(), callerConfigDto.getSid(), OperateRecordType.CALLERUSAGE_UPDATE_PASS, builder.toString());
        } catch (Exception e) {
            log.error("[error_recodeUpdateCallerUsageFinish]originCallerUsage={},callerConfigDto={},service={},caller={}", originCallerUsage, callerConfigDto, service, caller);
        }
    }
}
