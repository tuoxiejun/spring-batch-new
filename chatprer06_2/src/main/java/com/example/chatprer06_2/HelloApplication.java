package com.example.chatprer06_2;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class HelloApplication {


    public static void main(String[] args)  {
        SpringApplication.run(HelloApplication.class, args);
    }
}