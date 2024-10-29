package com.example.chatprer03_4;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class JobConfig2 {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(){
        return jobBuilderFactory.get("job1")
                .start(step1())
                .next(step2())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1())
                .listener(stepExecutionListener())
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(tasklet2())
                .build();
    }

    @Bean
    public Tasklet tasklet1() {
        return (stepContribution, chunkContext) ->{
            ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
            executionContext.put("key1", "value1");
            ExecutionContext executionContext1 = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            System.out.println("executionContext1 = "+ executionContext1);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet tasklet2() {
        return (stepContribution, chunkContext) ->{
            ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            System.out.println("executionContext" + executionContext);
            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
            System.out.println("stepExecution" + stepExecution);

            return RepeatStatus.FINISHED;
        };
    }

    public StepExecutionListener stepExecutionListener(){
        ExecutionContextPromotionListener executionContextPromotionListener = new ExecutionContextPromotionListener();
        executionContextPromotionListener.setKeys(new String[]{"key1"});
        return executionContextPromotionListener;
    }
}
