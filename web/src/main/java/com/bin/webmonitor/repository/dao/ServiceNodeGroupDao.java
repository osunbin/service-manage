package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.ServiceNodeGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceNodeGroupDao {
    /**
     *  添加分组节点
     *
     * @param serviceNodeGroups 节点列表
     */
    public void batchInsert(List<ServiceNodeGroup> serviceNodeGroups);

    /**
     * 根据gid查找节点
     * @param gid 分组编号
     * @return 节点列表
     */
    public List<ServiceNodeGroup> selectByGid(long gid);

    public List<ServiceNodeGroup> selectByIp(String ip);

    /**
     * 根据sid查找节点
     *
     * @return
     */
    public List<ServiceNodeGroup> selectBySid(int sid);

    /**
     * 根据sid, ip查找节点
     *
     * @param ip
     * @return
     */
    public List<ServiceNodeGroup> selectBySidIp(@Param("sid") long sid, @Param("ip") String ip);

    public Integer countBySidIp(@Param("sid") int sid, @Param("ip") String ip);

    /**
     * 删除分组节点
     *
     * @param list 节点列表
     */
    public void deleteBatch(List<ServiceNodeGroup> list);

    /**
     * 删除分组中所有节点
     *
     * @param gid 分组id
     * @return
     */
    public int deleteByGid(int gid);
}
