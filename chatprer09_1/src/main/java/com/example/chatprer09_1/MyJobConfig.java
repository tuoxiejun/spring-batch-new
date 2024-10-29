package com.example.chatprer09_1;

import com.example.chatprer09_1.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public FlatFileItemReader<Customer> myCustomerItemReader(@Value("#{jobParameters['input']}")Resource input){
        return new  FlatFileItemReaderBuilder<Customer>()
                .name("myCustomerItemReader")
                .delimited()
                .names(new String[] {"firstName",
                "middleInitial",
                "lastName",
                "address",
                "city",
                "state",
                "zip"})
                .targetType(Customer.class)
                .resource(input)
                .build();
    }

    @Bean
    public Step copyStep(){
        return stepBuilderFactory.get("copyStep")
                .<Customer, Customer>chunk(1)
                .reader(myCustomerItemReader(null))
                .writer(customerItemWriter(null))
                .build();
    }


    @Bean
    @StepScope
    public FlatFileItemWriter<Customer> customerItemWriter(@Value("#{jobParameters['output']}")Resource output){
        return new FlatFileItemWriterBuilder<Customer>()
                .name("customerItemWriter")
                .resource(output)
                .formatted()
                .format("%s %s lives at %s %s in %s, %s.")
                .names(new String[] {"firstName", "lastName", "address", "city", "state", "zip"})
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