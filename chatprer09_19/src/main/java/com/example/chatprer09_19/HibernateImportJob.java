package com.example.chatprer09_19;

import com.example.chatprer09_19.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
//    customerFile=input/customerWithEmail.csv
    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFlatFileItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFlatFileItemReader")
                .resource(inputFile)
                .delimited()
                .names("firstName",
                        "middleInitial",
                        "lastName",
                        "address",
                        "city",
                        "state",
                        "zip",
                        "email")
                .targetType(Customer.class)
                .build();

    }

    @Bean
    public StaxEventItemWriter<Customer> customerStaxEventItemWriter() {

        Map<String, Class> classHashMap = new HashMap<>();
        classHashMap.put("customer", Customer.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(classHashMap);
        marshaller.afterPropertiesSet();

        return new StaxEventItemWriterBuilder<Customer>()
                .name("customerStaxEventItemWriter")
                .marshaller(marshaller)
                .rootTagName("customers")
                .resource(new FileSystemResource("input/customer.xml"))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> customerJdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(dataSource))
                .sql("INSERT INTO CUSTOMER (first_name, " +
						"middle_initial, " +
						"last_name, " +
						"address, " +
						"city, " +
						"state, " +
						"zip, " +
						"email) " +
						"VALUES(:firstName, " +
						":middleInitial, " +
						":lastName, " +
						":address, " +
						":city, " +
						":state, " +
						":zip, " +
						":email)")
                .beanMapped()
                .build();
    }

    @Bean
    public CompositeItemWriter<Customer> customerCompositeItemWriter(){
       return new CompositeItemWriterBuilder<Customer>()
               .delegates(customerJdbcBatchItemWriter(null), customerStaxEventItemWriter())
               .build();
    }
    
    @Bean
    public Step importStep(){
        return stepBuilderFactory.get("importStep")
                .<Customer,Customer> chunk(10)
                .reader(customerFlatFileItemReader(null))
                .writer(customerCompositeItemWriter())
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