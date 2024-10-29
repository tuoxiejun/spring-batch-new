package com.example.chatprer07_5.batch;

import com.example.chatprer07_5.domain.Customer;
import com.example.chatprer07_5.domain.Transaction;
import org.springframework.batch.item.*;

import java.util.ArrayList;

public class CustomerFileReader implements ItemStreamReader<Customer> {

    private Object curItem = null;
    private ItemStreamReader<Object> delegate;

    public CustomerFileReader(ItemStreamReader<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (curItem == null){
            curItem = delegate.read();
        }
        Customer item = (Customer) curItem;
        curItem = null;
        if (item != null){
            item.setTransactions(new ArrayList<>());
            while (peek() instanceof Transaction){
                item.getTransactions().add((Transaction) peek());
                curItem = null;
            }
        }
        return item;
    }

    private Object peek() throws Exception {
        if (curItem == null){
            curItem = delegate.read();
        }
          return curItem;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);

    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}
