package com.example.chatprer07_15;

import com.example.chatprer07_15.batch.CustomerItemReader;
import com.example.chatprer07_15.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

//    @Bean
//    @StepScope
//    public StoredProcedureItemReader<Customer> customerItemReader(DataSource dataSource,@Value("#{jobParameters['city']}")String city){
//        return new StoredProcedureItemReaderBuilder<Customer>()
//                .name("customerItemReader")
//                .procedureName("customer_list")
//                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{city}))
//                .parameters(new SqlParameter("in_city", java.sql.Types.VARCHAR))
//                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
//                .dataSource(dataSource)
//                .build();
//    }

    @Bean
    public CustomerItemReader myCustomerItemReader(){
        CustomerItemReader customerItemReader = new CustomerItemReader();
        customerItemReader.setName("myreader");
        return customerItemReader;
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
                .reader(myCustomerItemReader())
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public Job copyJob(){
        return jobBuilderFactory.get("copyJob")
//                .incrementer(new RunIdIncrementer())
                .start(copyStep())
                .build();
    }
}