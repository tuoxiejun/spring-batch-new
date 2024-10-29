package com.example.chatprer08_6.batch;

import com.example.chatprer08_6.domain.Customer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

import java.lang.annotation.Annotation;

public class ZipCodeClassifier implements Classifier<Customer, ItemProcessor<Customer,Customer>> {

    private ItemProcessor<Customer,Customer> eventProcessor;
    private ItemProcessor<Customer,Customer> oddProcessor;

    public ZipCodeClassifier(ItemProcessor<Customer,Customer> eventProcessor, ItemProcessor<Customer,Customer> oddProcessor){
        this.eventProcessor = eventProcessor;
        this.oddProcessor = oddProcessor;
    }

    @Override
    public ItemProcessor<Customer, Customer> classify(Customer customer) {
        if (Integer.parseInt(customer.getZip()) % 2 == 0){
            return eventProcessor;
        }
        return oddProcessor;
    }
}
