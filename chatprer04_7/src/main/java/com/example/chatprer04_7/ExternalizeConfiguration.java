//package com.example.chatprer04_7;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.job.builder.FlowBuilder;
//import org.springframework.batch.core.job.flow.Flow;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.support.ListItemReader;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//
//@Configuration
//public class ExternalizeConfiguration {
//
//    @Autowired
//    JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    StepBuilderFactory stepBuilderFactory;
//
//
//    @Bean
//    public Flow preProcessingFlow(){
//        return new FlowBuilder<Flow>("preProcessingFlow").start(loadFileStep())
//                .next(loadCustomerFileStep())
//                .next(updateStartStep())
//                .build();
//    }
//
//    @Bean
//    public Job externalizeJob() {
//        return jobBuilderFactory.get("externalizeJob")
//                .start(preProcessingFlow())
//                .next(runBatch())
//                .end()
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    public Tasklet loadStockFile(){
//        return ((contribution, chunkContext) -> {
//            System.out.println("--------------------------------loadStockFile");
////            throw new RuntimeException("passTasklet error");
//            return RepeatStatus.FINISHED;
//        });
//    }
//
//    @Bean
//    public Tasklet loadCustomerFile(){
//        return ((contribution, chunkContext) -> {
//            System.out.println("---------------------------------loadCustomerFile");
//            return RepeatStatus.FINISHED;});
//    }
//    @Bean
//    public Tasklet updateStart(){
//        return ((contribution, chunkContext) -> {
//            System.out.println("-------------------------------------updateStart");
//            return RepeatStatus.FINISHED;});
//    }
//
//    @Bean
//    public Tasklet runBatchTasklet(){
//        return ((contribution, chunkContext) -> {
//            System.out.println("-------------------------------------runBatchTasklet");
//            return RepeatStatus.FINISHED;});
//    }
//
//
//
//
//
//    @Bean
//    public Step loadFileStep(){
//        return stepBuilderFactory.get("loadFileStep")
//                .tasklet(loadStockFile())
//                .build();
//    }
//    @Bean
//    public Step loadCustomerFileStep(){
//        return stepBuilderFactory.get("loadCustomerFileStep")
//                .tasklet(loadCustomerFile())
//                .build();
//    }
//    @Bean
//    public Step updateStartStep(){
//        return stepBuilderFactory.get("updateStartStep")
//                .tasklet(updateStart())
//                .build();
//    }
//
//    @Bean
//    public Step runBatch(){
//        return stepBuilderFactory.get("runBatch")
//                .tasklet(runBatchTasklet())
//                .build();
//    }
//
//
//}