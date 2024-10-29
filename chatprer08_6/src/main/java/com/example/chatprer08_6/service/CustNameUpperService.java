package com.example.chatprer08_6.service;

import com.example.chatprer08_6.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustNameUpperService {

    public Customer upper(Customer customer){
        Customer customer1 = new Customer(customer);
        customer1.setFirstName(customer.getFirstName().toUpperCase());
        customer1.setMiddleInitial(customer.getMiddleInitial().toUpperCase());
        customer1.setLastName(customer.getLastName().toUpperCase());
        return customer1;
    }
}
