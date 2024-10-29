package com.example.chatprer03_4;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.StepListenerFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class StepListenerConfigration {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job listenerJob() {
        return jobBuilderFactory.get("listenerJob")
                .start(listenerStep1())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step listenerStep1() {
        return stepBuilderFactory.get("listenerStep1")
                .<String, String>chunk(10)
                .reader(ListenerReader())
                .writer(listenerWriter())
                .listener(new LoggingStepStartStopListener1())
                .build();
    }

    @Bean
    public ItemReader<String> ListenerReader() {
        ArrayList<String> strings = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            strings.add("Item " + i);
        }
        return new ListItemReader<>(strings);
    }

    @Bean
    public ItemWriter<String> listenerWriter() {
        return items -> {
            System.out.println("itemSize: " + items.size());
            System.out.println("startItem: " + items.get(0));
            System.out.println("endItem: " + items.get(items.size() - 1));
        };
    }

}