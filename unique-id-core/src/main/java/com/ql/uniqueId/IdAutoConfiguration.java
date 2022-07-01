package com.ql.uniqueId;

import com.ql.uniqueId.exception.service.IdService;
import com.ql.uniqueId.exception.service.impl.IdServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author wanqiuli
 * @date 2022/7/1 14:51
 */
@Configuration
@EnableConfigurationProperties({IdProperties.class})
@AutoConfigureAfter({TransactionAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
public class IdAutoConfiguration {

    @Resource
    private IdProperties idProperties;


    @Bean
    public IdService idService() {
        IdServiceImpl idService = new IdServiceImpl();
        int step = idProperties.getStep();
        if (step > 0) {
            idService.setStep(step);
        }
        return idService;
    }
}
