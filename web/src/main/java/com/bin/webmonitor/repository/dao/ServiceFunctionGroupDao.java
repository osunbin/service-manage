package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.ServiceFunctionGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceFunctionGroupDao {
    public List<ServiceFunctionGroup> selectBySid(int sid);

    public int insert(@Param("sid") int sid, @Param("groupName") String groupName);

    public ServiceFunctionGroup selectById(int id);

    public void updateGroupName(@Param("groupName") String groupName, @Param("id") int id);

    public int deleteById(int id);
}
