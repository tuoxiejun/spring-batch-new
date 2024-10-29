package com.example.chatprer07_11;

import com.example.chatprer07_11.domain.Customer;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.database.HibernatePagingItemReader;
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder;
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

//    @Bean
//    @StepScope
//    public HibernateCursorItemReader<Customer> customerItemReader(EntityManagerFactory entityManagerFactory, @Value("#{jobParameters['city']}") String city){
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("city",city);
//
//        return new HibernateCursorItemReaderBuilder<Customer>()
//                .name("customerItemReader")
//                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
//                .queryString("from Customer where city = :city")
//                .parameterValues(param)
//                .build();
//    }

    @Bean
    @StepScope
    public HibernatePagingItemReader<Customer> customerItemReader(EntityManagerFactory entityManagerFactory, @Value("#{jobParameters['city']}") String city){
        HashMap<String, Object> param = new HashMap<>();
        param.put("city",city);

        return new HibernatePagingItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .queryString("from Customer where city = :city")
                .parameterValues(param)
                .pageSize(1)
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean queryProviderFactoryBean(DataSource dataSource){
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause("select *");
        sqlPagingQueryProviderFactoryBean.setFromClause("from customer");
        sqlPagingQueryProviderFactoryBean.setWhereClause("where city = :city");
        sqlPagingQueryProviderFactoryBean.setSortKey("lastName");
        return sqlPagingQueryProviderFactoryBean;
    }

    @Bean
    @StepScope
    public ArgumentPreparedStatementSetter citySetter(@Value("#{jobParameters['city']}")String city){
        return new ArgumentPreparedStatementSetter(new Object[]{city});
    }


    @Bean
    public HibernateItemWriter<Customer> hibernateItemWriter(EntityManagerFactory entityManager) {
        return new HibernateItemWriterBuilder<Customer>()
                .sessionFactory(entityManager.unwrap(SessionFactory.class))
                .build();
    }

    @Bean
    public Step copyStep(){
        return stepBuilderFactory.get("copyStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null,null))
                .writer(hibernateItemWriter(null))
                .build();
    }

    @Bean
    public Job copyJob(){
        return jobBuilderFactory.get("copyJob")
                .incrementer(new RunIdIncrementer())
                .start(copyStep())
                .build();
    }
}