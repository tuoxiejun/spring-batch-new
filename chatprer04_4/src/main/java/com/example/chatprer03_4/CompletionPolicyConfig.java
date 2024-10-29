//package com.example.chatprer03_4;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.support.ListItemReader;
//import org.springframework.batch.repeat.CompletionPolicy;
//import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
//import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
//import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//
//@Configuration
//public class CompletionPolicyConfig {
//
//    @Autowired
//    JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    StepBuilderFactory stepBuilderFactory;
//
//
//    @Bean
//    public Job chunkJob() {
//        return jobBuilderFactory.get("chunkJob")
//                .start(chunkStep1())
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    public Step chunkStep1() {
//        return stepBuilderFactory.get("chunkStep1")
//                .<String, String>chunk(completionPolicy())
//                .reader(chunkReader())
//                .writer(chunkWriter())
//                .build();
//    }
//
//    @Bean
//    public ItemReader<String> chunkReader() {
//        ArrayList<String> strings = new ArrayList<>(10000);
//        for (int i = 0; i < 10000; i++) {
//            strings.add("Item " + i);
//        }
//        return new ListItemReader<>(strings);
//    }
//
//    @Bean
//    public ItemWriter<String> chunkWriter() {
//        return items -> {
//            System.out.println("itemSize: " + items.size());
//            System.out.println("startItem: " + items.get(0));
//            System.out.println("endItem: " + items.get(items.size() - 1));
//        };
//    }
//
//    @Bean
//    public CompletionPolicy completionPolicy() {
//        CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();
//        compositeCompletionPolicy.setPolicies(
//                new CompletionPolicy[]{
//                        new SimpleCompletionPolicy(1000),
//                        new TimeoutTerminationPolicy(1)
//                }
//        );
//        return compositeCompletionPolicy;
//    }
//}