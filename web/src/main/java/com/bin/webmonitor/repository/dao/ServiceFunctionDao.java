package com.bin.webmonitor.repository.dao;

import com.bin.webmonitor.repository.domain.CircuitBreakMonitor;
import com.bin.webmonitor.repository.domain.ServiceFunction;
import com.bin.webmonitor.common.MethodKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class ServiceFunctionDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_SERVICE_FUNCTION = "insert into t_servicefunction (sid,fname,interface,lookup,generic_signature,general_signature,multi_impl,ip,createtime) values(?,?,?,?,?,?,?,?,?)";

    private static final String SELECT_BY_SID = "select * from t_servicefunction where sid = ? order by fname ";

    private static final String SELECT_BY_SID_GID = "select * from t_servicefunction where sid = ? and sfgid = ? order by fname desc";

    private static String SELECT_BY_ID = "select * from t_servicefunction where id = ?  ";

    private static String SELECT_BY_SID_FNAME = "select * from t_servicefunction where sid = ? and fname=? ";

    private static String INSERT_FUNCTION = "insert into t_servicefunction(sid,fname,ip,createTime,updateTime) values(?,?,?,now(),now())";

    private static String DELETE_BY_ID = "DELETE FROM t_servicefunction WHERE id=?";

    private static String UPDATE_GID_TO_DEFAULT = "UPDATE t_servicefunction SET sfgid=-1 WHERE sfgid=?";

    private static String UPDATE_SIGNATURE_BY_SID_AND_FNAME = "UPDATE t_servicefunction SET interface=?,lookup=?,generic_signature=?,general_signature=?,multi_impl=?,ip=?   WHERE sid=? AND fname=?";




    /**
     * 插入服务函数信息
     *
     * @param serviceFunction 上报的服务函数
     * @return boolean
     */
    public boolean add(ServiceFunction serviceFunction) {
        jdbcTemplate.update(INSERT_SERVICE_FUNCTION,
                serviceFunction.getSid(),
                serviceFunction.getGenericMethodSignature(),
                serviceFunction.getInterfaceName(),
                serviceFunction.getLookup(),
                serviceFunction.getGenericMethodSignature(),
                serviceFunction.getGeneralMethodSignature(),
                serviceFunction.isMultiImplDefault() ? 1 : 0,
                serviceFunction.getIp(),
                new Date());
        return true;
    }


    public List<ServiceFunction> getServiceFunctionsBySid(int sid) {

        return jdbcTemplate.query(SELECT_BY_SID,  new BeanPropertyRowMapper<>(ServiceFunction.class),new Object[]{sid});
    }


    public List<ServiceFunction> getServiceFunctionBySidGid(int sid, int gid) {

        return jdbcTemplate.query(SELECT_BY_SID_GID, new BeanPropertyRowMapper<>(ServiceFunction.class), new Object[]{sid, gid});
    }


    public ServiceFunction getServiceFunctionsByid(long functionId) {
        return jdbcTemplate.queryForObject(SELECT_BY_ID, new BeanPropertyRowMapper<>(ServiceFunction.class), new Object[]{functionId});
    }



    public ServiceFunction getServiceFunctionsBySidAndFname(int sid, String functionName) {
        return jdbcTemplate.queryForObject(SELECT_BY_SID_FNAME, new BeanPropertyRowMapper<>(ServiceFunction.class), new Object[]{sid, functionName});
    }


    public void addFunctions(int sid, List<String> fnames, String ip) {
        jdbcTemplate.batchUpdate(INSERT_FUNCTION, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String fname = fnames.get(i);
                ps.setInt(1, sid);
                ps.setString(2, fname);
                ps.setString(3, ip);
            }

            @Override
            public int getBatchSize() {
                return fnames.size();
            }
        });
    }

    public void addMethodKeys(int sid, List<MethodKey> methodKeys, String ip) {
        jdbcTemplate.batchUpdate(INSERT_SERVICE_FUNCTION, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String funcName = methodKeys.get(i).getLookup() + "." + methodKeys.get(i).getMethodSignatureWithGenericTypes();
                ps.setInt(1, sid);
                ps.setString(2, funcName);
                ps.setString(3, methodKeys.get(i).getInterfaceName());
                ps.setString(4, methodKeys.get(i).getLookup());
                ps.setString(5, methodKeys.get(i).getMethodSignatureWithGenericTypes());
                ps.setString(6, methodKeys.get(i).getMethodSignatureWithoutGenericTypes());
                ps.setInt(7, methodKeys.get(i).isMultiImplDefault() ? 1 : 0);
                ps.setString(8, ip);
            }

            @Override
            public int getBatchSize() {
                return methodKeys.size();
            }
        });
    }

    public int insertFunctionSignature(int sid, MethodKey methodKeys, String ip) {
        return jdbcTemplate.update(INSERT_SERVICE_FUNCTION,
                sid,
                methodKeys.getLookup() + "." + methodKeys.getMethodSignatureWithGenericTypes(),
                methodKeys.getInterfaceName(),
                methodKeys.getLookup(),
                methodKeys.getMethodSignatureWithGenericTypes(),
                methodKeys.getMethodSignatureWithoutGenericTypes(),
                methodKeys.isMultiImplDefault() ? 1 : 0,
                ip,
                new Date());
    }

    public int updateFunctionSignature(int sid, MethodKey methodKeys, String ip){
        return jdbcTemplate.update(UPDATE_SIGNATURE_BY_SID_AND_FNAME,
                methodKeys.getInterfaceName(),
                methodKeys.getLookup(),
                methodKeys.getMethodSignatureWithGenericTypes(),
                methodKeys.getMethodSignatureWithoutGenericTypes(),
                methodKeys.isMultiImplDefault() ? 1 : 0,
                ip,
                sid,
                methodKeys.getLookup() + "." + methodKeys.getMethodSignatureWithGenericTypes());
    }

    public void updateFunctionGroup(String fids, int groupId) {
        jdbcTemplate.update("update t_servicefunction set sfgid=? where id in (" + fids + ")", groupId);
    }

    public List<ServiceFunction> multiGet(String fids) {
        return jdbcTemplate.query("select * from t_servicefunction where id in (" + fids + ")", new BeanPropertyRowMapper<>(ServiceFunction.class));
    }

    public void updateGid2Default(int gid) {
        jdbcTemplate.update(UPDATE_GID_TO_DEFAULT,gid);
    }

    public int deleteById(int id) {
        return jdbcTemplate.update(DELETE_BY_ID, id);
    }
}
