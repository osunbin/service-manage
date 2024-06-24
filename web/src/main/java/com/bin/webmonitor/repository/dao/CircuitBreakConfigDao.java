package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.CircuitBreakConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CircuitBreakConfigDao {
    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    CircuitBreakConfig queryCircuitBreakConfigById(@Param("id") int id);

    /**
     * 根据(cid,sid,fid)查询
     *
     * @param cid
     * @param sid null表示不限
     * @param fid null表示不限
     * @return
     */
    List<CircuitBreakConfig> queryCircuitBreakConfigs(@Param("cid") Integer cid,  @Param("sid") Integer sid,
                                                      @Param("fid") Integer fid);

    /**
     * 增量式更新数据
     *
     * @param id
     * @param enabled 是否开启熔断机制。false：不是；true：是；null表示不更新此字段
     * @param forceOpened 是否强制开启熔断机制。0：不是；1：是；null表示不更新此字段
     * @param forceClosed 是否强制关闭熔断机制。0：不是；1：是；null表示不更新此字段
     * @param slideWindowInSeconds 滑动窗口时长（秒)。null表示不更新此字段
     * @param requestVolumeThreshold 窗口内进行熔断判断的最小请求个数（请求数过少则不会启动熔断机制）。null表示不更新此字段
     * @param errorThresholdPercentage 错误比例，百分比，取值范围为[0,100]。null表示不更新此字段
     * @param sleepWindowInMilliseconds 熔断时间窗口时长（毫秒)。null表示不更新此字段
     * @return 更新条数
     */
    int updateById(@Param("id") int id, @Param("enabled") Boolean enabled,
                   @Param("forceOpened") Boolean forceOpened,  @Param("forceClosed") Boolean forceClosed,
                   @Param("slideWindowInSeconds") Integer slideWindowInSeconds,
                   @Param("requestVolumeThreshold") Integer requestVolumeThreshold,
                   @Param("errorThresholdPercentage") Integer errorThresholdPercentage,
                   @Param("sleepWindowInMilliseconds") Integer sleepWindowInMilliseconds);

    /**
     * 保存单条数据
     *
     * @param circuitBreakConfigPo
     * @return 保存条数
     */
    int save(CircuitBreakConfig circuitBreakConfigPo);

    /**
     * 根据ID单条删除
     *
     * @param id 要删除的记录ID
     * @return 删除条数
     */
    int deleteById(@Param("id") int id);
}
