package com.bin.webmonitor.component;

import com.bin.webmonitor.command.ConfigChangeCommand;
import com.bin.webmonitor.command.RejectCommand;
import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.DataGrid;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.model.vo.ServiceCallerVO;
import com.bin.webmonitor.repository.cache.LocalCaller;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.CallerUsageDao;
import com.bin.webmonitor.repository.dao.GroupDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.domain.Caller;
import com.bin.webmonitor.repository.domain.CallerUseage;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceCallerService {

    private static Logger logger = LoggerFactory.getLogger(ServiceCallerService.class);

    @Autowired
    private CallerUsageDao callerUsageDao;

    @Autowired
    private LocalCaller localCaller;

    @Autowired
    private LocalService localService;


    @Autowired
    private GroupDao groupDao;

    @Autowired
    private SendCommand sendCommand;



    @Autowired
    private ServiceFunctionDao serviceFunctionDao;

    public DataGrid<ServiceCallerVO> list(Pageable pageable, int sid, int cid, String function, int orgId) {

        DataGrid<ServiceCallerVO> serviceCallerVoDataGrid = new DataGrid<>();
        serviceCallerVoDataGrid.setCurrent(pageable.getPageNumber());
        serviceCallerVoDataGrid.setRowCount(pageable.getPageSize());

        ServiceInstance service = localService.getById(sid);
        if (service == null) {
            return serviceCallerVoDataGrid;
        }

        List<ServiceFunction> functions = serviceFunctionDao.getServiceFunctionsBySid(sid);
        List<String> functionNames = new ArrayList<>();
        if (!CollectionUtil.isEmpty(functions)) {
            functionNames = functions.stream().map(ServiceFunction::getFname).collect(Collectors.toList());
        }


        List<Group> groups = groupDao.selectBySid(sid);

        List<CallerUseage> callerUsages = callerUsageDao.selectById(pageable, sid, cid);


        if (CollectionUtil.isEmpty(callerUsages)) {
            return serviceCallerVoDataGrid;
        }
        int count = callerUsageDao.count(sid, cid);
        List<ServiceCallerVO> scvos = vo(callerUsages, function, true, groups, functionNames);
        serviceCallerVoDataGrid.setRows(scvos);
        serviceCallerVoDataGrid.setTotal(count);

        return serviceCallerVoDataGrid;
    }

    private List<ServiceCallerVO> vo(List<CallerUseage> callerUseages1, String fn, boolean update, List<Group> groups, List<String> functions) {
        List<ServiceCallerVO> serviceCallerVos = new LinkedList<>();
        for (CallerUseage callerUseage : callerUseages1) {
            try {
                ServiceCallerVO serviceCallerVO = new ServiceCallerVO();
                Caller caller = localCaller.getById(callerUseage.getCid());
                serviceCallerVO.setCallerName(caller.getCallerName());
                serviceCallerVO.setCid(callerUseage.getCid());
                serviceCallerVO.setFunction(fn);
                int gid = callerUseage.getGid();
                serviceCallerVO.setGroupId(gid);
                String groupName = groupDao.selectById(gid).getGroupName();
                serviceCallerVO.setGroupName(groupName);
                serviceCallerVO.setReject(callerUseage.isReject());
                serviceCallerVO.setUpdate(update);
                serviceCallerVO.setGroups(groups);
                serviceCallerVO.setFunctions(functions);
                serviceCallerVos.add(serviceCallerVO);
            } catch (Throwable e) {
                logger.error("caller usage is {}", callerUseage, e);
            }
        }
        return serviceCallerVos;
    }

    public ApiResult<Boolean> updateGroup(ServiceInstance service, int cid, int gid) {
        Group group = groupDao.selectById(gid);
        if (group == null || group.getSid() != service.getId()) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("分组id错误");
        }
        CallerUseage callerUsage = callerUsageDao.selectByCidSid(cid, service.getId());
        if (callerUsage == null) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("没有调用关系");
        }
        if (callerUsage.getGid() == gid) {
            return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
        } else {
            callerUsageDao.updateGroup(service.getId(), cid, gid);
            Caller caller = localCaller.getById(cid);
            sendCommand.sendClient(caller.getCallerKey(), new ConfigChangeCommand().setService(service.getServiceName()));
        }
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    public ApiResult<Boolean> updateReject(ServiceInstance service, int cid, boolean reject) {
        CallerUseage callerUsage = callerUsageDao.selectByCidSid(cid, service.getId());
        if (callerUsage == null) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("没有调用关系");
        }
        if (callerUsage.isReject() == reject) {
            return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
        } else {
            callerUsageDao.updateReject(service.getId(), cid, reject);
            Caller caller = localCaller.getById(cid);
            sendCommand.sendService(service.getServiceName(), new RejectCommand().setCallerKey(caller.getCallerKey()));
        }
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

}
