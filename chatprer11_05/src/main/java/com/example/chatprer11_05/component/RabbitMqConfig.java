//package com.example.chatprer11_05.component;
//
//import org.springframework.amqp.support.converter.DefaultClassMapper;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.batch.integration.partition.StepExecutionRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMqConfig {
//
//    @Bean
//    public MessageConverter jacsonMessageConverter() {
//        DefaultClassMapper classMapper = new DefaultClassMapper();
//        classMapper.setDefaultType(StepExecutionRequest.class);
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        jackson2JsonMessageConverter.setClassMapper(classMapper);
//        return jackson2JsonMessageConverter;
//    }
//}
