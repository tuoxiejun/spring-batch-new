package com.example.chatprer09_16;

import com.example.chatprer09_16.component.CustomerOutputFileSuffixCreator;
import com.example.chatprer09_16.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.item.jms.builder.JmsItemReaderBuilder;
import org.springframework.batch.item.mail.SimpleMailMessageItemWriter;
import org.springframework.batch.item.mail.builder.SimpleMailMessageItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

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
    public StaxEventItemWriter<Customer> customerStaxEventItemWriter() {
        Map<String,Class> map = new HashMap<>();
        map.put("customer", Customer.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(map);
        marshaller.afterPropertiesSet();

        return new StaxEventItemWriterBuilder<Customer>()
                .marshaller(marshaller)
                .rootTagName("customers")
                .standalone(false)
                .name("customerStaxEventItemWriter")
                .build();
    }

    @Bean
    public MultiResourceItemWriter<Customer> multiResourceItemWriter(CustomerOutputFileSuffixCreator fileSuffixCreator){
       return new MultiResourceItemWriterBuilder<Customer>()
               .name("multiResourceItemWriter")
               .resource(new FileSystemResource("input/customer"))
               .resourceSuffixCreator(fileSuffixCreator)
               .delegate(customerStaxEventItemWriter())
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