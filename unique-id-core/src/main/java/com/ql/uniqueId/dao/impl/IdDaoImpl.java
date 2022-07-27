package com.ql.uniqueId.dao.impl;

import com.ql.uniqueId.dao.IdDao;
import com.ql.uniqueId.domain.SequencePo;
import com.ql.uniqueId.exception.IdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

/**
 * @author wanqiuli
 * @date 2022/7/26 20:24
 */
@Slf4j
public class IdDaoImpl implements IdDao {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager platformTransactionManager;
    private final TransactionDefinition transactionDefinition;


    public IdDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        if (jdbcTemplate.getDataSource() == null) {
            throw new IdException("[id utils] datasource null, init fail");
        }
        this.platformTransactionManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");
        this.transactionDefinition = defaultTransactionDefinition;
    }

    @Override
    public List<String> getAllSequenceInfo() {
        String sql = "select t_name from sequence";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public void addSequence(String tableName, Integer defaultStep) {
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        String sql;
        try {
            sql = "insert into sequence values(?, ?, ?)";
            int execute = jdbcTemplate.update(sql, ps -> {
                ps.setLong(1, 0);
                ps.setInt(2, defaultStep);
                ps.setString(3, tableName);
            });
            if (execute != 1) {
                throw new IdException("[id utils] insert new sequence error");
            }
            platformTransactionManager.commit(transactionStatus);
        } catch (IdException e) {
            log.error(e.getMessage());
            platformTransactionManager.rollback(transactionStatus);
            throw e;
        } catch (Exception e) {
            log.error("[id utils] update sql error, rollback");
            platformTransactionManager.rollback(transactionStatus);
            throw new IdException("[id utils] update sql error, rollback");
        }
    }

    @Override
    public SequencePo updateAndGet(String tableName) {
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        String sql;
        try {
            sql = "update sequence set current_max_id = current_max_id + step where t_name = ?";
            int execute = jdbcTemplate.update(sql, ps -> {
                ps.setString(1, tableName);
            });
            if (execute != 1) {
                throw new IdException("[id utils] update sequence error");
            }
            platformTransactionManager.commit(transactionStatus);
        } catch (IdException e) {
            log.error(e.getMessage());
            platformTransactionManager.rollback(transactionStatus);
            throw e;
        } catch (Exception e) {
            log.error("[id utils] update sql error, rollback");
            platformTransactionManager.rollback(transactionStatus);
            throw new IdException("[id utils] update sql error, rollback");
        }
        sql = "select current_max_id, step from sequence where t_name = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SequencePo.class), tableName);
    }
}
