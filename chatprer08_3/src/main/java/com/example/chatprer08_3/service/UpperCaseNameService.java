package com.example.chatprer08_3.service;

import com.example.chatprer08_3.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class UpperCaseNameService {

    public Customer upperCase(Customer customer){
        Customer cust = new Customer(customer);
        cust.setFirstName(customer.getFirstName().toUpperCase());
        cust.setMiddleInitial(cust.getMiddleInitial().toUpperCase());
        cust.setLastName(cust.getLastName().toUpperCase());
        return cust;
    }
}
