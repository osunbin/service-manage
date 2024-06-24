package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.ServiceInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface ServiceDao {

    /**
     * 新增服务记录
     *
     * @param service
     * @return
     * @throws Exception
     */
    boolean insert(ServiceInstance service);

    /**
     * 修改服务记录
     *
     * @param service
     * @return
     * @throws Exception
     */
    boolean updateService(ServiceInstance service);


    boolean deleteService(int id);

    List<ServiceInstance> selectAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     * @throws Exception
     */
    ServiceInstance selectById(int id);

    /**
     * 根据服务名称查询，例如：UMC、IMC
     *
     * @param serviceName
     * @return
     * @throws Exception
     */
    ServiceInstance selectByName(String serviceName);

    List<ServiceInstance> serviceList(ServiceInstance service, Pageable pageable);

    List<ServiceInstance> serviceListForTran(ServiceInstance service, Pageable pageable);

    int countServiceList(ServiceInstance service);

    int countServiceListForTran(ServiceInstance service);


    
}
