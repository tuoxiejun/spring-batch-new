package com.example.chatprer09_12;

import com.example.chatprer09_12.domain.Customer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableBatchProcessing
@SpringBootApplication
public class HelloApplication {


    public static void main(String[] args)  {
        SpringApplication.run(HelloApplication.class, args);
    }
}