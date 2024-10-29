package com.example.chatprer07_2.batch;

import com.example.chatprer07_2.domain.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {
    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        Customer customer = new Customer();
        customer.setFirstName(fieldSet.readString("firstName"));
        customer.setLastName(fieldSet.readString("lastName"));
        customer.setMiddleInitial(fieldSet.readString("middleInitial"));
        customer.setAddress(fieldSet.readString("addressNumber") + fieldSet.readString("street"));
        customer.setCity(fieldSet.readString("city"));
        customer.setZipCode(fieldSet.readString("zipCode"));
        customer.setState(fieldSet.readString("state"));
        return customer;
    }
}
