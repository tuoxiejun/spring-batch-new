package com.example.chatprer07_11.batch;

import org.hibernate.SessionFactory;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Component
public class HibernateBatchConfigurer extends DefaultBatchConfigurer {
    private DataSource dataSource;
    private SessionFactory sessionFactory;
    private PlatformTransactionManager platformTransactionManager;

    public HibernateBatchConfigurer(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        super(dataSource);
        this.dataSource = dataSource;
        this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.platformTransactionManager = new HibernateTransactionManager(sessionFactory);
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return platformTransactionManager;
    }
}
