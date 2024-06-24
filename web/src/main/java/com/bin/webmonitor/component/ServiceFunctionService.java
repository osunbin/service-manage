package com.bin.webmonitor.component;

import com.bin.webmonitor.common.Constants;
import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.common.util.DelimiterHelper;
import com.bin.webmonitor.controller.ApiResult;
import com.bin.webmonitor.model.vo.ServiceFunctionGroupVO;
import com.bin.webmonitor.repository.dao.CallerFuncUsageDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionDao;
import com.bin.webmonitor.repository.dao.ServiceFunctionGroupDao;
import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.repository.domain.ServiceFunctionGroup;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ServiceFunctionService {

    private static Logger logger = LoggerFactory.getLogger(ServiceFunctionService.class);

    private static final int DEFAULT_GROUP = -1;
    @Autowired
    private ServiceFunctionGroupDao serviceFunctionGroupDao;
    @Autowired
    private ServiceFunctionDao serviceFunctionDao;
    @Autowired
    private CallerFuncUsageDao callerFuncUsageDao;




    public List<ServiceFunctionGroupVO> list(int sid) {
        List<ServiceFunctionGroupVO> serviceFunctionGroupVos = new ArrayList<>(200);
        List<ServiceFunctionGroup> serviceFunctionGroups = serviceFunctionGroupDao.selectBySid(
                sid);
        Map<Integer, String> groupIdGroupNameIndex = new HashMap<>();
        if (serviceFunctionGroups != null) {
            serviceFunctionGroups.forEach(
                    serviceFunctionGroup -> groupIdGroupNameIndex.put(serviceFunctionGroup.getId(),
                            serviceFunctionGroup.getGroupName()));
        }
        List<ServiceFunction> serviceFunctions = serviceFunctionDao.getServiceFunctionsBySid(
                sid);
        if (serviceFunctions != null) {
            Map<Integer, List<ServiceFunction>> gidGroup = serviceFunctions.stream().collect(
                    Collectors.groupingBy(ServiceFunction::getSfgid));
            gidGroup.forEach((gid, sfs) -> {
                ServiceFunctionGroupVO serviceFunctionGroupVO = new ServiceFunctionGroupVO();
                serviceFunctionGroupVO.setFunctionNum(sfs.size());
                serviceFunctionGroupVO.setGroupName(
                        gid == -1 ? "默认分组" : groupIdGroupNameIndex.get(gid));
                serviceFunctionGroupVO.setFunctions(sfs);
                serviceFunctionGroupVO.setGid(gid);
                serviceFunctionGroupVO.setCanSelect(gidGroup.get(DEFAULT_GROUP));
                serviceFunctionGroupVos.add(serviceFunctionGroupVO);
            });
        }
        return serviceFunctionGroupVos;
    }

    public ApiResult<Boolean> addGroup(ServiceInstance service, String groupName,
                                       String fids) {

        int groupId;
        try {
            groupId = serviceFunctionGroupDao.insert(service.getId(), groupName);
        } catch (DuplicateKeyException e) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage(
                    groupName + "已经存在");
        }

        List<ServiceFunction> serviceFunctions = serviceFunctionDao.multiGet(fids);
        if (serviceFunctions == null) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("fids error");
        }
        if (serviceFunctions.stream().anyMatch(sf -> sf.getSid() != service.getId())) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("fids error");
        }

        serviceFunctionDao.updateFunctionGroup(fids, groupId);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS).setResult(true);
    }

    public ApiResult<Boolean> updateGroup(ServiceInstance service, int gid,
                                          String groupName, String fids) {


        ServiceFunctionGroup serviceFunctionGroup = serviceFunctionGroupDao.selectById(gid);
        if (serviceFunctionGroup == null || serviceFunctionGroup.getSid() != service.getId()) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("gid error");
        }

        if (!serviceFunctionGroup.getGroupName().equals(groupName)) {
            try {
                serviceFunctionGroupDao.updateGroupName(groupName, gid);
            } catch (DuplicateKeyException e) {
                return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage(
                        groupName + "已经存在");
            }
        }

        List<ServiceFunction> serviceFunctions = serviceFunctionDao
                .getServiceFunctionBySidGid(
                        service.getId(), gid);
        if (serviceFunctions == null) {
            serviceFunctionDao.updateFunctionGroup(fids, gid);
        } else {
            Set<Integer> fromFids = Stream.of(fids.split(",")).map(Integer::parseInt).collect(
                    Collectors.toSet());
            List<Integer> delete = new ArrayList<>(20);
            for (ServiceFunction serviceFunction : serviceFunctions) {
                if (!fromFids.contains(serviceFunction.getId())) {
                    delete.add(serviceFunction.getId());
                } else {
                    fromFids.remove(serviceFunction.getId());
                }
            }

            if (fromFids.size() != 0) {
                logger.info("update service {} group {} add fids {}",
                        service.getServiceName(), groupName, fromFids);
                serviceFunctionDao.updateFunctionGroup(DelimiterHelper.join(fromFids), gid);
            }

            if (delete.size() != 0) {
                logger.info(" update service {} group {} delete fids {}",
                        service.getServiceName(), groupName, delete);
                serviceFunctionDao.updateFunctionGroup(DelimiterHelper.join(delete),
                        DEFAULT_GROUP);
            }
        }
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS);
    }

    public ApiResult<Boolean> deleteGroup( ServiceInstance service, int gid,
                                          String groupName) {
        ServiceFunctionGroup serviceFunctionGroup = serviceFunctionGroupDao.selectById(gid);
        if (serviceFunctionGroup == null || serviceFunctionGroup.getSid() != service.getId()) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("gid error");
        }

        serviceFunctionDao.updateGid2Default(gid);
        serviceFunctionGroupDao.deleteById(gid);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS);
    }

    public ApiResult<Boolean> deleteFunction( ServiceInstance service, int fid) {
        ServiceFunction function = serviceFunctionDao.getServiceFunctionsByid(fid);
        if (function == null || function.getSid() != service.getId()) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("函数id错误");
        }
        List<CallerFunctionUseage> cfs = callerFuncUsageDao.selectByFid(fid);
        if (!CollectionUtil.isEmpty(cfs)) {
            return new ApiResult<Boolean>().setCode(Constants.NOT_SUCCESS).setMessage("存在调用关系，不能删除");
        }
        serviceFunctionDao.deleteById(fid);
        return new ApiResult<Boolean>().setCode(Constants.SUCCESS);
    }



}
