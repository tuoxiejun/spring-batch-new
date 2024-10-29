package com.example.chatprer05_1;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Component
//@Configuration
public class CustomBatchConfigurer extends DefaultBatchConfigurer {

    @Autowired
    @Qualifier("dataSource2")
    private DataSource batchDataSource;

    @Autowired
    @Qualifier("costomTransactionManager")
    private PlatformTransactionManager transactionManager;

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(batchDataSource);
    }

    @Override
    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(this.batchDataSource);
        factory.setTransactionManager(transactionManager);
        factory.setTablePrefix("FOO_");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

}
