package com.example.chatprer07_1;

import com.example.chatprer07_1.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
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
                .resource(resource)
                .fixedLength()
                .columns(new Range[]{new Range(1,11), new Range(12, 12), new Range(13, 22),
                new Range(23, 26), new Range(27,46), new Range(47,62), new Range(63,64),
                new Range(65,69)})
                .names("firstName", "middleInitial", "lastName",
                        "addressNumber", "street", "city", "state","zipCode")
                .targetType(Customer.class)
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