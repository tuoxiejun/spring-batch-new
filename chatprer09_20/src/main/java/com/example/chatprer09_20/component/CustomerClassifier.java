package com.example.chatprer09_20.component;

import com.example.chatprer09_20.domain.Customer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.classify.Classifier;

public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {

    private ItemWriter<Customer> xmlItemWriter;
    private ItemWriter<Customer> jdbcItemWriter;

    public CustomerClassifier(StaxEventItemWriter<Customer> xmlItemWriter, JdbcBatchItemWriter<Customer> jdbcItemWriter) {
        this.xmlItemWriter = xmlItemWriter;
        this.jdbcItemWriter = jdbcItemWriter;
    }

    @Override
    public ItemWriter<Customer> classify(Customer customer) {
        if(customer.getState().matches("^[A-M].*")){
            return xmlItemWriter;
        }else {
            return jdbcItemWriter;
        }
    }
}
