package com.example.chatprer05_2;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobExplorer jobExplorer;

    @Bean
    public Tasklet myTasklet() {
        return new MyTasklet(jobExplorer);
    }

    @Bean
    public Step myStep() {
        return stepBuilderFactory.get("myStep")
                .tasklet(myTasklet())
                .build();
    }

    @Bean
    public Job myJob() {
        return jobBuilderFactory.get("myJob")
                .start(myStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }


}