package com.ql.uniqueId.dao.impl;

import com.ql.uniqueId.dao.IdDao;
import com.ql.uniqueId.domain.SequencePo;
import com.ql.uniqueId.exception.IdException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author wanqiuli
 * @date 2022/7/26 20:24
 */
public class IdDaoImpl implements IdDao {

    private JdbcTemplate jdbcTemplate;

    public IdDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> getAllSequenceInfo() {
        String sql = "select t_name from sequence";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void addSequence(String tableName, Integer defaultStep) {
        String sql = "insert into sequence values(?, ?, ?)";
        int execute = jdbcTemplate.update(sql, ps -> {
            ps.setLong(1, 0);
            ps.setInt(2, defaultStep);
            ps.setString(3, tableName);
        });
        if (execute != 1) {
            throw new IdException("[id utils] insert new sequence error");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public SequencePo updateAndGet(String tableName) {
        String sql = "update sequence set current_max_id = current_max_id + step where t_name = ?";
        int execute = jdbcTemplate.update(sql, ps -> {
            ps.setString(1, tableName);
        });
        if (execute != 1) {
            throw new IdException("[id utils] insert update sequence error");
        }
        sql = "select current_max_id, step from sequence where t_name = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SequencePo.class), tableName);
    }
}
