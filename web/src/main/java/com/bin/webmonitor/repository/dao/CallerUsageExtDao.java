package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.CallerUsageExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CallerUsageExtDao {
    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    CallerUsageExt queryById(@Param("id") int id);

    /**
     * 根据<cid,sid>查询
     *
     * @param cid
     * @param sid
     * @return
     */
    CallerUsageExt queryByCidSid(@Param("cid") int cid, @Param("sid") int sid);

    /**
     * 根据cid或者sid查询
     */
    List<CallerUsageExt> queryByCidOrSid(@Param("cid") Integer cid,  @Param("sid") Integer sid);

    /**
     * @param configType
     * @return
     */
    List<CallerUsageExt> queryByConfigType(@Param("configType") int configType);

    /**
     * 更新数据
     *
     * @param CallerUsageExt
     * @return 更新条数
     */
    int updateByCidSid(CallerUsageExt CallerUsageExt);

    /**
     * 保存单条数据
     *
     * @param CallerUsageExt
     * @return 保存条数
     */
    int save(CallerUsageExt CallerUsageExt);

    /**
     * 批量保存
     *
     * @param CallerUsageExtList
     * @return 保存条数
     */
    int batchSave(List<CallerUsageExt> CallerUsageExtList);

    /**
     * 根据ID单条删除
     *
     * @param id
     *            要删除的记录ID
     * @return 删除条数
     */
    int deleteById(@Param("id") long id);

    int deleteByCidSid(@Param("cid") int cid, @Param("sid") int sid);
}
