package com.example.chatprer08_5.batch;

import com.example.chatprer08_5.domain.Customer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UniqueLastNameValidator extends ItemStreamSupport implements Validator<Customer> {
    private Set<String> lastNames = new HashSet<>();
    @Override
    public void validate(Customer value) throws ValidationException {
        if (lastNames.contains(value.getLastName())){
            throw new ValidationException("lastName:"+ value.getLastName() +" is Duplicate");
        }
        lastNames.add(value.getLastName());

    }

    @Override
    public void open(ExecutionContext executionContext) {
        String lastName = getExecutionContextKey("lastName");
        if (executionContext.containsKey(lastName)){
            lastNames = (Set<String>) executionContext.get(lastName);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        String lastName = getExecutionContextKey("lastName");
        executionContext.put(lastName,lastNames);
    }
}
