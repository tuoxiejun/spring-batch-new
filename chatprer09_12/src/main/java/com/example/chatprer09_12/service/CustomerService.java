package com.example.chatprer09_12.service;

import com.example.chatprer09_12.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    public void logCustomer(Customer customer){
        System.out.println("I just logged customer: " + customer);
    }
}
