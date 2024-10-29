package com.example.chatprer11_07;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableBatchProcessing
@SpringBootApplication
@EnableAspectJAutoProxy
public class HelloApplication {


    public static void main(String[] args)  {
        SpringApplication.run(HelloApplication.class, args);
    }
}