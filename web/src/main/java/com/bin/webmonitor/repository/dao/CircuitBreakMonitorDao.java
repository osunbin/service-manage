package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.CircuitBreakMonitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface CircuitBreakMonitorDao {

    /**
     * 根据(cid,sid,fid)查询
     *
     * @param cid
     * @param sid null表示不限
     * @param fid null表示不限
     * @param pageable null表示不限
     * @return
     */
    List<CircuitBreakMonitor> queryCircuitBreakMonitors(@Param("cid") Integer cid,  @Param("sid") Integer sid,
                                                           @Param("fid") Integer fid,  @Param("pageable") Pageable pageable);

    int queryTotalCount(@Param("cid") int cid,  @Param("sid") Integer sid,
                         @Param("fid") Integer fid);

    /**
     * 批量保存
     *
     * @param circuitBreakMonitorPos
     * @return 保存条数
     */
    int batchSave(List<CircuitBreakMonitor> circuitBreakMonitorPos);

    /**
     * 根据ID单条删除
     *
     * @param id 要删除的记录ID
     * @return 删除条数
     */
    int deleteById(@Param("id") long id);
}
