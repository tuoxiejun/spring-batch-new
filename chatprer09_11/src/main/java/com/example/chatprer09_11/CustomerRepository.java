package com.example.chatprer09_11;

import com.example.chatprer09_11.domain.Customer;
import org.springframework.data.repository.CrudRepository;


public interface CustomerRepository extends CrudRepository<Customer,Long> {

}
