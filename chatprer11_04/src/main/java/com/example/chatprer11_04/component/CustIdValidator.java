package com.example.chatprer11_04.component;

import com.example.chatprer11_04.domain.CustomerNameUpdate;
import com.example.chatprer11_04.domain.CustomerUpdate;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustIdValidator implements Validator<CustomerUpdate> {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final String  FIND_CUSTOMER = "SELECT COUNT(1) FROM CUSTOMER WHERE CUSTOMER_ID = :customerId";
    public CustIdValidator(DataSource dataSource){
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    @Override
    public void validate(CustomerUpdate value) throws ValidationException {
        Map<String, Long> param = new HashMap<>();
        param.put("customerId", value.getCustomerId());

        Long count = namedParameterJdbcTemplate.queryForObject(FIND_CUSTOMER, param, Long.class);

        if (count == null || count == 0){
            System.out.println("id" + value.getCustomerId() + " cannot be found !");
            throw new ValidationException(String.format("Customer id %s was not able to be found", value.getCustomerId() ));
        }
    }
}
