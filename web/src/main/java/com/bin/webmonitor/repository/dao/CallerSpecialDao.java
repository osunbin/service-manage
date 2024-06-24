package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.CallerSpecialAttr;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface CallerSpecialDao {

    int deleteByPrimaryKey(Integer id);

    int insert(CallerSpecialAttr callerSpecialAttr);

    int insertSelective(CallerSpecialAttr callerSpecialAttr);

    int updateByPrimaryKeySelective(CallerSpecialAttr callerSpecialAttr);

    int updateByPrimaryKey(CallerSpecialAttr callerSpecialAttr);

    int selectCount(CallerSpecialAttr callerSpecialAttr);

    CallerSpecialAttr selectByPrimaryKey(Integer id);

    List<CallerSpecialAttr> selectPage(CallerSpecialAttr callerSpecialAttr, Pageable pageable);

    /**
     * 根据cid 和sid获取 调用者灰度配置 列表
     */
    default List<CallerSpecialAttr> selectByCidAndSid(int sid, int cid) {
        CallerSpecialAttr entity = new CallerSpecialAttr();
        entity.setSid(sid);
        entity.setCid(cid);
        return selectPage(entity, null);
    }

    /**
     * 根据sid获取 调用者灰度配置 列表
     */
    default List<CallerSpecialAttr> selectBySid(int sid) {
        CallerSpecialAttr entity = new CallerSpecialAttr();
        entity.setSid(sid);
        return selectPage(entity, null);
    }
}
