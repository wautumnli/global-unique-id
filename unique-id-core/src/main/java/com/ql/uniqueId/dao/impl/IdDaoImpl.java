package com.ql.uniqueId.dao.impl;

import com.ql.uniqueId.dao.IdDao;
import com.ql.uniqueId.domain.IdEntity;
import com.ql.uniqueId.exception.IdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author wanqiuli
 * @date 2022/7/1 10:55
 */
@Slf4j
public class IdDaoImpl implements IdDao {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;


    public IdDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = jdbcTemplate.getDataSource();
    }

    @Override
    public List<IdEntity> getAllIdEntity() {
        String sql = "select current_id, table_name from table_unique_id";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(IdEntity.class));
    }

    @Override
    public void insert(IdEntity idEntity) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            String sql = "insert into table_unique_id values (?,?)";
            ps = conn.prepareStatement(sql);
        } catch (SQLException exception) {
            throw new IdException("Global-unique-id: " + exception.getMessage());
        } finally {
            JdbcUtils.closeStatement(ps);
            if (conn != null) {
                try {
                    conn.commit();
                } catch (SQLException exception) {
                    throw new IdException("Global-unique-id: " + exception.getMessage());
                } finally {
                    JdbcUtils.closeConnection(conn);
                }
            }
        }
    }

    @Override
    public Long updateAndGetId(String tableName, int step) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            String sql = "update table_unique_id set current_id = LAST_INSERT_ID(`current_id` + ?) where table_name = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, step);
            ps.setString(2, tableName);
            ps.executeUpdate();
            rs = ps.executeQuery("select last_insert_id()");
            if (!rs.next()) {
                throw new IdException("Global-Unique-Id: get last_insert_id fail!");
            }
            return rs.getLong(1);
        } catch (Exception e) {
            throw new IdException("global-unique-id: error, " + e.getMessage());
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            if (conn != null) {
                try {
                    conn.commit();
                } catch (Exception e) {
                    throw new IdException("global-unique-id: error, " + e.getMessage());
                } finally {
                    JdbcUtils.closeConnection(conn);
                }
            }
        }
    }
}
