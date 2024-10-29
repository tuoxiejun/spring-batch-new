package com.example.chatprer09_15;

import com.example.chatprer09_15.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
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
    @StepScope
    public FlatFileItemReader<Customer> customerFileReader(
            @Value("#{jobParameters['customerFile']}")Resource inputFile) {

        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[] {"firstName",
                        "middleInitial",
                        "lastName",
                        "address",
                        "city",
                        "state",
                        "zip",
                        "email"})
                .targetType(Customer.class)
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<Customer> customerBatchWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(dataSource))
                .beanMapped()
                .sql("INSERT INTO CUSTOMER (first_name, middle_initial, last_name, address, city, state, zip, email) " +
                        "VALUES(:firstName, :middleInitial, :lastName, :address, :city, :state, :zip, :email)")
                .build();
    }


    @Bean
    public JdbcCursorItemReader<Customer> customerCursorItemReader(DataSource dataSource){
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("customerCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM customer")
                .beanRowMapper(Customer.class)
                .build();
    }

    @Bean
    public ItemProcessor<Customer,SimpleMailMessage> customerProcessor(){
        return customer -> {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(customer.getEmail());
            simpleMailMessage.setSubject("Welcome to our store!");
            simpleMailMessage.setText("Dear " + customer.getFirstName() + " " + customer.getLastName() + ",\n\nThank you for joining our store!");
            simpleMailMessage.setFrom("1351028814@qq.com");
            return simpleMailMessage;
        };
    }

    @Bean
    public SimpleMailMessageItemWriter emailIterWriter(MailSender mailSender){
       return new SimpleMailMessageItemWriterBuilder()
               .mailSender(mailSender)
               .build();
    }
    
    @Bean
    public Step importStep(){
        return stepBuilderFactory.get("importStep")
                .<Customer,Customer> chunk(10)
                .reader(customerFileReader(null))
                .writer(customerBatchWriter(null))
                .build();
    }

    @Bean
    public Step emailStep(){
        return stepBuilderFactory.get("emailStep")
                .<Customer,SimpleMailMessage> chunk(10)
                .reader(customerCursorItemReader(null))
                .processor(customerProcessor())
                .writer(emailIterWriter(null))
                .build();
    }

    @Bean
    public Job emailJob() throws Exception {
        return this.jobBuilderFactory.get("emailJob")
                .incrementer(new RunIdIncrementer())
                .start(importStep())
                .next(emailStep())
                .build();
    }
}