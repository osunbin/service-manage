package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.ClientConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClientConfigDao {
    int insertClientConfig(ClientConfig clientConfig);

    int updateClientConfigByCidAndIp(@Param("cid") int cid, @Param("ip") String ip, @Param("usageConfig") String usageConfig);

    ClientConfig selectClientConfigByCidAndIp(@Param("cid") int cid, @Param("ip") String ip);

    int deleteByCid(int cid);
}
