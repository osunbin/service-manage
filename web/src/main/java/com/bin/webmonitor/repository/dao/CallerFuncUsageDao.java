package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.CallerFunctionUseage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface CallerFuncUsageDao {

    int deleteByPrimaryKey(Integer id);

    int insert(CallerFunctionUseage callerfuncusagepo);

    int insertSelective(CallerFunctionUseage callerfuncusagepo);

    CallerFunctionUseage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CallerFunctionUseage callerfuncusagepo);

    int updateByPrimaryKey(CallerFunctionUseage callerfuncusagepo);

    int selectCount(CallerFunctionUseage callerfuncusagepo);

    List<CallerFunctionUseage> selectPage(CallerFunctionUseage callerfuncusagepo, Pageable pageable);

    void batchUpdate(List<CallerFunctionUseage> list);

    /**
     * 根据cid 和sid获取调用方调用服务的函数列表
     */
    default List<CallerFunctionUseage> selectByCidAndSid(int cid, int sid) {
        CallerFunctionUseage entity = new CallerFunctionUseage();
        entity.setSid(sid);
        entity.setCid(cid);
        return selectPage(entity, null);
    }

    default List<CallerFunctionUseage> selectByFid(int fid) {
        CallerFunctionUseage entity = new CallerFunctionUseage();
        entity.setFid(fid);
        return selectPage(entity, null);
    }

    default List<CallerFunctionUseage> selectAll() {
        return selectPage(new CallerFunctionUseage(), null);
    }
}
