package com.example.chatprer07_15.batch;

import com.example.chatprer07_15.domain.Customer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerItemReader extends ItemStreamSupport implements ItemReader<Customer> {
    private List<Customer> customers;

    private int curIndex;

    private String INDEX_KEY = "currenet.index.customers";


    private String [] firstNames = {"Michael", "Warren", "Ann", "Terrence",
            "Erica", "Laura", "Steve", "Larry"};
    private String middleInitial = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String [] lastNames = {"Gates", "Darrow", "Donnelly", "Jobs",
            "Buffett", "Ellison", "Obama"};
    private String [] streets = {"4th Street", "Wall Street", "Fifth Avenue",
            "Mt. Lee Drive", "Jeopardy Lane",
            "Infinite Loop Drive", "Farnam Street",
            "Isabella Ave", "S. Greenwood Ave"};
    private String [] cities = {"Chicago", "New York", "Hollywood", "Aurora",
            "Omaha", "Atherton"};
    private String [] states = {"IL", "NY", "CA", "NE"};


    private Random generator = new Random();

    public CustomerItemReader() {
        customers = new ArrayList<>();
        curIndex = 0;

        for (int i = 0; i < 100; i++) {
            customers.add(buildCustomer());
        }
    }

    private Customer buildCustomer() {
        Customer customer = new Customer();
        customer.setId((long) generator.nextInt(Integer.MAX_VALUE));
        customer.setFirstName(
                firstNames[generator.nextInt(firstNames.length - 1)]);
        customer.setMiddleInitial(
                String.valueOf(middleInitial.charAt(
                        generator.nextInt(middleInitial.length() - 1))));
        customer.setLastName(
                lastNames[generator.nextInt(lastNames.length - 1)]);
        customer.setAddress(generator.nextInt(9999) + " " +
                streets[generator.nextInt(streets.length - 1)]);
        customer.setCity(cities[generator.nextInt(cities.length - 1)]);
        customer.setState(states[generator.nextInt(states.length - 1)]);
        customer.setZipCode(String.valueOf(generator.nextInt(99999)));
        return customer;
    }


    public Customer read() {
        Customer customer = null;

        if (curIndex == 50){
            throw new RuntimeException("No more customers-----------------------------");
        }

        if (curIndex < customers.size()) {
            customer = customers.get(curIndex++);
        }

        return customer;
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (executionContext.containsKey(getExecutionContextKey(INDEX_KEY))){
            int index = executionContext.getInt(getExecutionContextKey(INDEX_KEY));
            if (index == 50) {
                curIndex = 51;
            }else {
                curIndex = index;
            }
        }else {
            curIndex = 0;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        String executionContextKey = getExecutionContextKey(INDEX_KEY);
        executionContext.putInt(executionContextKey, curIndex);
    }
}
