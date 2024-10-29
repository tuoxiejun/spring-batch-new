package com.example.chatprer07_13;

import com.example.chatprer07_13.batch.CustomerRowMapper;
import com.example.chatprer07_13.domain.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.StoredProcedureItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.StoredProcedureItemReaderBuilder;
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
import org.springframework.jdbc.core.SqlParameter;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public StoredProcedureItemReader<Customer> customerItemReader(DataSource dataSource,@Value("#{jobParameters['city']}")String city){
        return new StoredProcedureItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .procedureName("customer_list")
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{city}))
                .parameters(new SqlParameter("in_city", java.sql.Types.VARCHAR))
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .dataSource(dataSource)
                .build();
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
                .reader(customerItemReader(null,null))
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