package com.example.chatprer09_18;

import com.example.chatprer09_18.component.CustomerOutputFileSuffixCreator;
import com.example.chatprer09_18.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
public class HibernateImportJob {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;


    public HibernateImportJob(JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public JdbcPagingItemReader<Customer> customerJdbcPagingItemReader(DataSource dataSource) {
        HashMap<String, Order> stringOrderHashMap = new HashMap<>();
        stringOrderHashMap.put("id", Order.ASCENDING);
        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("customerJdbcPagingItemReader")
                .dataSource(dataSource)
                .selectClause("select * ")
                .fromClause("from customer")
                .sortKeys(stringOrderHashMap)
                .pageSize(100)
                .beanRowMapper(Customer.class)
                .build();

    }

    @Bean
    public FlatFileItemWriter<Customer> customerStaxEventItemWriter(CustomFooterCallback customFooterCallback) {

        return new FlatFileItemWriterBuilder<Customer>()
                .delimited()
                .delimiter(",")
                .names("firstName","middleInitial", "lastName", "address", "city", "state", "zip")
                .headerCallback(writer -> writer.write("firstName,middleInitial, lastName, address, city, state, zip"))
                .footerCallback(customFooterCallback)
                .name("customerStaxEventItemWriter")
                .build();
    }

    @Bean
    public MultiResourceItemWriter<Customer> multiResourceItemWriter(CustomerOutputFileSuffixCreator fileSuffixCreator){
       return new MultiResourceItemWriterBuilder<Customer>()
               .name("multiResourceItemWriter")
               .resource(new FileSystemResource("input/customer"))
               .resourceSuffixCreator(fileSuffixCreator)
               .delegate(customerStaxEventItemWriter(null))
               .itemCountLimitPerResource(100)
               .build();
    }
    
    @Bean
    public Step importStep(){
        return stepBuilderFactory.get("importStep")
                .<Customer,Customer> chunk(10)
                .reader(customerJdbcPagingItemReader(null))
                .writer(multiResourceItemWriter(null))
                .build();
    }


    @Bean
    public Job emailJob() throws Exception {
        return this.jobBuilderFactory.get("emailJob")
                .incrementer(new RunIdIncrementer())
                .start(importStep())
                .build();
    }
}