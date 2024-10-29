package com.example.chatprer09_14.compment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsConfiguration {

    @Bean
    public MessageConverter jacsonMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }


    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
        cachingConnectionFactory.afterPropertiesSet();

        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        jmsTemplate.setMessageConverter(jacsonMessageConverter());
        jmsTemplate.setDefaultDestinationName("customers");
        jmsTemplate.setReceiveTimeout(5000L);
        return jmsTemplate;
    }
}
