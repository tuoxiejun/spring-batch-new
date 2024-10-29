package com.example.chatprer09_11;

import com.example.chatprer09_11.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = Customer.class)
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
                        "zip"})
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public RepositoryItemWriter<Customer> hibernateItemWriter(CustomerRepository customerRepository) {
        return new RepositoryItemWriterBuilder<Customer>()
                .repository(customerRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step hibernateFormatStep() throws Exception {
        return this.stepBuilderFactory.get("hibernateFormatStep")
                .<Customer, Customer>chunk(10)
                .reader(customerFileReader(null))
                .writer(hibernateItemWriter(null))
                .build();
    }

    @Bean
    public Job hibernateFormatJob() throws Exception {
        return this.jobBuilderFactory.get("hibernateFormatJob")
                .start(hibernateFormatStep())
                .build();
    }
}