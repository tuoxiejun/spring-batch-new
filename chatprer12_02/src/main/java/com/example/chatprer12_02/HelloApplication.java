package com.example.chatprer12_02;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@EnableBatchProcessing
@SpringBootApplication
@EnableRetry
public class HelloApplication {


    public static void main(String[] args)  {
        SpringApplication.run(HelloApplication.class, args);
    }
}