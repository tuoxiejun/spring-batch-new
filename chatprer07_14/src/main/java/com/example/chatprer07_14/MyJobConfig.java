package com.example.chatprer07_14;

import com.example.chatprer07_14.batch.CustomerService;
import com.example.chatprer07_14.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.StoredProcedureItemReader;
import org.springframework.batch.item.database.builder.StoredProcedureItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;

import javax.sql.DataSource;

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
    public ItemReaderAdapter<Customer> itemReaderAdapter(CustomerService customerService){
        ItemReaderAdapter<Customer> it = new ItemReaderAdapter<>();
        it.setTargetMethod("getCustomers");
        it.setTargetObject(customerService);
        return it;
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
                .reader(itemReaderAdapter(null))
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