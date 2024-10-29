package com.example.chatprer11_02;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

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
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step1----------start");
                    Thread.sleep(5000);
                    System.out.println("stp1------------end");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2----------start");
                    Thread.sleep(10000);
                    System.out.println("stp2------------end");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step3----------start");
                    System.out.println("stp3------------end");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Job parallelStepsJob(){
        Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(step2())
                .build();

        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(step1())
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return this.jobBuilderFactory.get("parallelStepsJob")
                .start(parallelFlow)
                .next(step3())
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }


}