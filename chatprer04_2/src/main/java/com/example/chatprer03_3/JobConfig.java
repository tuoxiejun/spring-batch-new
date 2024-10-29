package com.example.chatprer03_3;

import com.example.chatprer03_3.listener.JobLoggerListener;
import com.example.chatprer03_3.listener.JobLoggerListener2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(){
        return jobBuilderFactory.get("job1")
                .start(step1())
                .incrementer(new RunIdIncrementer())
//                .listener(new JobLoggerListener())
                .listener(new JobLoggerListener2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet())
                .build();
    }

    @Bean
    public Tasklet tasklet() {
        return (stepContribution, chunkContext) ->{
            System.out.println("Tasklet is executed");
            return RepeatStatus.FINISHED;
        };
    }
}
