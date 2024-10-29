package com.example.chatprer07_8.batch;

import com.example.chatprer07_8.domain.Customer;
import com.example.chatprer07_8.domain.Transaction;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.util.ArrayList;

public class CustomerFileReader implements ResourceAwareItemReaderItemStream<Customer> {

    private Object curItem = null;
    private ResourceAwareItemReaderItemStream<Object> delegate;

    public CustomerFileReader(ResourceAwareItemReaderItemStream<Object> delegate) {
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

    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);

    }

    public void close() throws ItemStreamException {
        delegate.close();
    }

    @Override
    public void setResource(Resource resource) {
        delegate.setResource(resource);
    }
}
