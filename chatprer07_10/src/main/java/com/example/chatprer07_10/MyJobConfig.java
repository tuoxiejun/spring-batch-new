package com.example.chatprer07_10;

import com.example.chatprer07_10.batch.CustomerRowMapper;
import com.example.chatprer07_10.domain.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.HashMap;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> customerItemReader(DataSource dataSource, @Value("#{jobParameters['city']}") String city, PagingQueryProvider queryProvider){
        HashMap<String, Object> param = new HashMap<>();
        param.put("city",city);

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .pageSize(100)
                .queryProvider(queryProvider)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .dataSource(dataSource)
                .parameterValues(param)
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
    public ItemWriter customerItemWriter(){
        return items -> {
            items.forEach(System.out::println);
        };
    }

    @Bean
    public Step copyStep(){
        return stepBuilderFactory.get("copyStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null,null,null))
                .writer(customerItemWriter())
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