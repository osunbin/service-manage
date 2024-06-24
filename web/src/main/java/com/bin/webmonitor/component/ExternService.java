package com.bin.webmonitor.component;

import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.repository.cache.LocalService;
import com.bin.webmonitor.repository.dao.GroupDao;
import com.bin.webmonitor.repository.domain.Group;
import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternService {

    @Autowired
    private LocalService localService;
    @Autowired
    private GroupDao groupDao;

    public ServiceResult<ServiceInstance> getServiceInfo(String serviceName) {
        ServiceInstance service = localService.getByName(serviceName);
        return new ServiceResult<>(ServiceResult.SUCCESS, service);
    }

    public ServiceResult<List<Group>> getGroupInfo(String serviceName) {
        ServiceInstance service = localService.getByName(serviceName);
        List<Group> group = groupDao.selectBySid(service.getId());
        return new ServiceResult<>(ServiceResult.SUCCESS, group);
    }
}
