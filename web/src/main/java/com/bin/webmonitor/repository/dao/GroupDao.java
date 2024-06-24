package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface GroupDao {
    /**
     * 新增服务分组
     *
     * @param group
     * @return
     */
    public int insert(Group group);

    /**
     * 根据id查询分组。
     *
     * 如果你希望查询的 Group 带有Group.ips 请使用 GroupService。
     */
    public Group selectById(long id);

    /**
     * 根据组名sid查询分组信息
     *
     * @param sid
     * @return
     */
    public List<Group> selectBySid(int sid);

    /**
     * 根据(sid,groupName)查询分组信息
     *
     * @param sid
     */
    public Group selectBySidAndGroupName(@Param("sid") int sid, @Param("groupName") String groupName);

    /**
     * 根据分组id删除 该分组
     *
     * @param id
     * @return
     */
    public int delete(int id);

    public void updateGroupTime(int id);

    public int updateStatus(@Param("id") int id, @Param("status") int status);
}
