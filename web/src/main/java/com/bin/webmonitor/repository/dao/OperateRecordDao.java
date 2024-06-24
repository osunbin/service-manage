package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.OperateRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 *  记录用户操作
 */
@Mapper
public interface OperateRecordDao {

    int insert(OperateRecord operateRecord);

    List<OperateRecord> selectPage(OperateRecord operateRecord, Pageable pageable);
}
