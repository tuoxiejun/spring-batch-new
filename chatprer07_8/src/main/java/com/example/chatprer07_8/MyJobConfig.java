package com.example.chatprer07_8;

import com.example.chatprer07_8.domain.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public JsonItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}")Resource resource){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        JacksonJsonObjectReader<Customer> customerJacksonJsonObjectReader = new JacksonJsonObjectReader<Customer>(Customer.class);
        customerJacksonJsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(resource)
                .jsonObjectReader(customerJacksonJsonObjectReader)
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
                .<Customer, Customer>chunk(2)
                .reader(customerItemReader(null))
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