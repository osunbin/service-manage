package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.CallerUseage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface CallerUsageDao {

    int deleteByPrimaryKey(Integer id);

    int insert(CallerUseage callerUseage);

    int insertSelective(CallerUseage callerUseage);

    int updateByPrimaryKeySelective(CallerUseage callerUseage);

    int updateByPrimaryKey(CallerUseage callerUseage);

    void updateGroup(@Param("sid") int sid, @Param("cid") int cid, @Param("gid") int gid);

    void updateReject(@Param("sid") int sid, @Param("cid") int cid, @Param("reject") boolean reject);

    int selectCount(CallerUseage callerUseage);

    int count(@Param("sid") int sid, @Param("cid") int cid);

    CallerUseage selectByPrimaryKey(Integer id);

    CallerUseage selectByCidSid(@Param("cid") int cid, @Param("sid") int sid);

    List<CallerUseage> selectPage(CallerUseage callerUseage, Pageable pageable);

    List<CallerUseage> selectAll();



    List<CallerUseage> selectById(@Param("pageable") Pageable pageable, @Param("sid") int sid, @Param("cid") int cid);

    /**
     * 根据cid 和sid获取 调用者配置 列表
     */
    default List<CallerUseage> selectByCidAndSid(int sid, int cid) {
        CallerUseage entity = new CallerUseage();
        entity.setSid(sid);
        entity.setCid(cid);
        return selectPage(entity, null);
    }

    /**
     * 根据cid 和gid获取 调用者配置 列表
     */
    default List<CallerUseage> selectByCidAndGid(int sid, int gid) {
        CallerUseage entity = new CallerUseage();
        entity.setSid(sid);
        entity.setGid(gid);
        return selectPage(entity, null);
    }

    /**
     * 根据sid获取 调用者配置 列表
     */
    default List<CallerUseage> selectBySid(int sid) {
        CallerUseage entity = new CallerUseage();
        entity.setSid(sid);
        return selectPage(entity, null);
    }

    /**
     * 根据cid获取 调用者配置 列表
     */
    default List<CallerUseage> selectByCid(int cid) {
        CallerUseage entity = new CallerUseage();
        entity.setCid(cid);
        return selectPage(entity, null);
    }

    default boolean getDegrade(int cid, int sid) {
        List<CallerUseage> list = selectByCidAndSid(sid, cid);
        return null != list && list.size() > 0 ? false : list.get(0).isDegrade();
    }

    default boolean getReject(int cid, int sid) {
        List<CallerUseage> list = selectByCidAndSid(sid, cid);
        return null != list && list.size() > 0 ? false : list.get(0).isReject();
    }

}
