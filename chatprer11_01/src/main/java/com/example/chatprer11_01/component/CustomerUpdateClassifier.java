package com.example.chatprer11_01.component;

import com.example.chatprer11_01.domain.CustomerAddressUpdate;
import com.example.chatprer11_01.domain.CustomerContactUpdate;
import com.example.chatprer11_01.domain.CustomerNameUpdate;
import com.example.chatprer11_01.domain.CustomerUpdate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.classify.Classifier;
import org.springframework.stereotype.Component;

public class CustomerUpdateClassifier implements Classifier<CustomerUpdate, ItemWriter<? super CustomerUpdate>> {
    private final JdbcBatchItemWriter<CustomerUpdate> jdbcBatchItemWriter1;
    private final JdbcBatchItemWriter<CustomerUpdate> jdbcBatchItemWriter2;
    private final JdbcBatchItemWriter<CustomerUpdate> jdbcBatchItemWriter3;
    public CustomerUpdateClassifier(JdbcBatchItemWriter<CustomerUpdate> jdbcBatchItemWriter1,
                                    JdbcBatchItemWriter<CustomerUpdate> jdbcBatchItemWriter2,
                                    JdbcBatchItemWriter<CustomerUpdate> jdbcBatchItemWriter3){
        this.jdbcBatchItemWriter1 = jdbcBatchItemWriter1;
        this.jdbcBatchItemWriter2 = jdbcBatchItemWriter2;
        this.jdbcBatchItemWriter3 = jdbcBatchItemWriter3;
    }
    @Override
    public ItemWriter<? super CustomerUpdate> classify(CustomerUpdate customerUpdate) {
        if (customerUpdate instanceof CustomerNameUpdate){
            return jdbcBatchItemWriter1;
        } else if (customerUpdate instanceof CustomerAddressUpdate) {
            return jdbcBatchItemWriter2;
        } else if (customerUpdate instanceof CustomerContactUpdate) {
            return jdbcBatchItemWriter3;
        }else {
            throw new IllegalArgumentException("Invalid type: " + customerUpdate.getClass().getCanonicalName());
        }
    }
}
