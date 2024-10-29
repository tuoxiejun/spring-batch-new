package com.example.chatprer11_03;

import com.example.chatprer11_03.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.Arrays;
import java.util.concurrent.Future;

@Configuration
public class BackJobConfiguration {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;


    public BackJobConfiguration(JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public StaxEventItemReader<Transaction> transactionStaxEventItemReader(
            @Value("#{jobParameters['transactionInput']}")Resource resource){
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Transaction.class);
        return new StaxEventItemReaderBuilder<Transaction>()
                .name("transactionStaxEventItemReader")
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(jaxb2Marshaller)
                .build();
    }

    @Bean
    public ItemProcessor<Transaction,Transaction> transactionItemProcessor(){
        return item -> {
            System.out.println("start processor");
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(5000);
            System.out.println("end processor");
            return item;
        };
    }

    @Bean
    public AsyncItemProcessor<Transaction,Transaction> transactionAsyncItemProcessor(){
        AsyncItemProcessor<Transaction, Transaction> it = new AsyncItemProcessor<>();
        it.setDelegate(transactionItemProcessor());
        it.setTaskExecutor(new SimpleAsyncTaskExecutor("transactionAsyncItemProcessor"));
        return it;
    }

    @Bean
    public ItemWriter<Transaction> transactionItemWriter(){
        return items -> {
            System.out.println("start writer");
            System.out.println(Arrays.toString(items.toArray()));
            System.out.println("end writer");

        };
    }

    @Bean
    public AsyncItemWriter<Transaction> transactionAsyncItemWriter(){
        AsyncItemWriter<Transaction> it = new AsyncItemWriter<>();
        it.setDelegate(transactionItemWriter());
        return it;
    }


    @Bean
    public Step transactionStep() {
        return stepBuilderFactory.get("transactionStep")
                .<Transaction, Future<Transaction>>chunk(10)
                .reader(transactionStaxEventItemReader(null))
                .processor(transactionAsyncItemProcessor())
                .writer(transactionAsyncItemWriter())
                .build();
    }

    @Bean
    public Job customerUpdateJob() throws Exception {
        return jobBuilderFactory.get("customerUpdateJob")
                .start(transactionStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}