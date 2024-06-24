package com.bin.webmonitor.repository.dao;


import com.bin.webmonitor.repository.domain.Caller;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface CallerDao {




    int deleteByPrimaryKey(Integer id);

    int insert(Caller caller);

    int insertSelective(Caller caller);

    List<Caller> selectAll();

    Caller selectByPrimaryKey(Integer id);

    Caller selectByKey(String callerKey);

    Caller selectByName(String callerName);

    int updateByPrimaryKeySelective(Caller caller);

    int updateByPrimaryKey(Caller caller);

    int selectCount(Caller caller);

    List<Caller> selectPage(Caller caller, Pageable pageable);
}
