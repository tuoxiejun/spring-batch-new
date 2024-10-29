package com.example.chatprer09_13.service;

import com.example.chatprer09_13.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    public void logCustomer(Customer customer){
        System.out.println("I just logged customer: " + customer);
    }

    public void logCustomerAddress(String address,String city,String state,String zip){
        System.out.println("I just logged customer: " + address + " " + city + " " + state + " " + zip);
    }
}
