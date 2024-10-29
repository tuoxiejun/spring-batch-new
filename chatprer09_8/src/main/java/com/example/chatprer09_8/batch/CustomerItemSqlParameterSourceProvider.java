package com.example.chatprer09_8.batch;

import com.example.chatprer09_8.domain.Customer;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class CustomerItemSqlParameterSourceProvider implements ItemSqlParameterSourceProvider<Customer> {
    @Override
    public SqlParameterSource createSqlParameterSource(Customer item) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(item);
        return sqlParameterSource;
    }
}
