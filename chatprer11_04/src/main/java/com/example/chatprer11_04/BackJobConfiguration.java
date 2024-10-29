package com.example.chatprer11_04;

import com.example.chatprer11_04.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.JspTemplateAvailabilityProvider;
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
            @Value("#{stepExecutionContext['file']}")Resource resource) throws InterruptedException {
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
    @StepScope
    public MultiResourcePartitioner partitioner(@Value("#{jobParameters['inputFiles']}") Resource[] resources){
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        partitioner.setKeyName("file");
        partitioner.setResources(resources);
        return partitioner;
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler() throws InterruptedException {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(step1());
        partitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor("partitionHandler"));
        return partitionHandler;
    }

    @Bean
    ItemProcessor<Transaction,Transaction> transactionItemProcessor(){
        return item -> {
        System.out.println("-----------------processorstart  " + Thread.currentThread().getName());
            Thread.sleep(1000);
            System.out.println("-----------------processorend  " + Thread.currentThread().getName());
            return item;
        };
    }
    @Bean
    public ItemWriter<Transaction> transactionItemWriter(){
        return items -> {
            System.out.println("-----------------writerstart  " + Thread.currentThread().getName());
            Thread.sleep(1000);
            System.out.println("-----------------writerend  " + Thread.currentThread().getName());
        };
    }

    @Bean
    public Step step1() throws InterruptedException {
        return stepBuilderFactory.get("step1")
                .<Transaction, Transaction>chunk(10)
                .reader(transactionStaxEventItemReader(null))
                .processor(transactionItemProcessor())
                .writer(transactionItemWriter())
                .build();
    }

    @Bean
    public Step partitionedMaster() throws InterruptedException {
        return stepBuilderFactory.get("partitionedMaster")
                .partitioner(step1().getName(), partitioner(null))
                .partitionHandler(partitionHandler())
                .build();
    }

    @Bean
    public Job partitionedJob() throws Exception {
        return jobBuilderFactory.get("partitionedJob")
                .start(partitionedMaster())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}