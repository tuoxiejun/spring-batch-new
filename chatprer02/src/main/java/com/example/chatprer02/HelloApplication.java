package com.example.chatprer02;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;

@EnableBatchProcessing
@SpringBootApplication
public class HelloApplication {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    Environment environment;

    @Bean
    public Job job(){
        return jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }

    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    String driverClassName = environment.getProperty("spring.datasource.driver-class-name");
                    System.out.println("spring.datasource.driver-class-name: " + driverClassName);
                    System.out.println("Hello World");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    public static void main(String[] args)  {
        SpringApplication.run(HelloApplication.class, args);
    }
}