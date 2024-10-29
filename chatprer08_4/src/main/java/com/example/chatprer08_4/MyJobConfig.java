package com.example.chatprer08_4;

import com.example.chatprer08_4.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.ScriptItemProcessor;
import org.springframework.batch.item.support.builder.ScriptItemProcessorBuilder;
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
    @StepScope
    public ScriptItemProcessor<Customer,Customer> processorAdapter(@Value("#{jobParameters['script']}")Resource input){
        return new ScriptItemProcessorBuilder<Customer,Customer>()
                .scriptResource(input)
                .itemBindingVariableName("cust")
                .build();
    }

    @Bean
    public Step copyStep(){
        return stepBuilderFactory.get("copyStep")
                .<Customer, Customer>chunk(1)
                .reader(myCustomerItemReader(null))
                .processor(processorAdapter(null))
                .writer(customerItemWriter())
                .build();
    }


    @Bean
    public ItemWriter customerItemWriter(){
        return items -> {
            items.forEach(System.out::println);
        };
    }

    @Bean
    public Job copyJob(){
        return jobBuilderFactory.get("copyJob")
//                .incrementer(new RunIdIncrementer())
                .start(copyStep())
                .build();
    }
}