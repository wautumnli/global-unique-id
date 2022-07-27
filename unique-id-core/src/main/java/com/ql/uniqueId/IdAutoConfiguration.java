package com.ql.uniqueId;

import com.ql.uniqueId.dao.impl.IdDaoImpl;
import com.ql.uniqueId.exception.IdException;
import com.ql.uniqueId.service.IdService;
import com.ql.uniqueId.service.impl.IdServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author wanqiuli
 * @date 2022/7/1 14:51
 */
@Configuration
@AutoConfigureAfter({TransactionAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
public class IdAutoConfiguration {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public IdService idService() {
        IdServiceImpl idService = new IdServiceImpl();
        IdDaoImpl idDao = new IdDaoImpl(jdbcTemplate);
        idService.setIdDao(idDao);
        if (!idService.init()) {
            throw new IdException("[id utils] init fail");
        }
        return idService;
    }
}
