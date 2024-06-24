package com.bin.webmonitor.repository.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class JdbcTemplateAdapter extends JdbcTemplate {

    public JdbcTemplateAdapter(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object[] args) throws DataAccessException {
        try {
            return super.query(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        try {
            return super.query(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object[] args) throws DataAccessException {
        try {
            return super.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        try {
            return super.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
