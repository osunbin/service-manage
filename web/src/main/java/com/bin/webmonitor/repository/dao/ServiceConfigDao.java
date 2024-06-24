package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.ServiceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ServiceConfigDao {

    /**
     * insert date,if exist then update
     *
     * @param serviceconfig
     * @return affect rows
     */
    int insertServiceConfig(ServiceConfig serviceconfig);

    int updateServiceConfigBySidAndIp(@Param("sid") int sid, @Param("ip") String ip, @Param("config") String config, @Param("log4j") String log4j);

    ServiceConfig selectServiceConfigBySidAndIp(@Param("sid") int sid, @Param("ip") String ip);

    int deleteBySid(int sid);

    int updateExtBySidAndIp(@Param("sid") int sid, @Param("ip") String ip, @Param("ext") String ext);


}
