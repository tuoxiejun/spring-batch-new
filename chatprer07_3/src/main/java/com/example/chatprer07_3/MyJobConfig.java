package com.example.chatprer07_3;

import com.example.chatprer07_3.batch.CustomerFieldSetMapper;
import com.example.chatprer07_3.batch.CustomerLineTokenizer;
import com.example.chatprer07_3.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.batch.api.chunk.ItemReader;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['inputFile']}")Resource resource){
        return new FlatFileItemReaderBuilder<Customer>().name("customerItemReader")
                .lineTokenizer(new CustomerLineTokenizer())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .resource(resource)
                .build();
    }

    @Bean
    public ItemWriter<Customer> customerItemWriter(){
        return items -> {
            items.forEach(System.out::println);
        };
    }

    @Bean
    public Step copyStep(){
        return stepBuilderFactory.get("copyStep")
                .<Customer, Customer>chunk(10)
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