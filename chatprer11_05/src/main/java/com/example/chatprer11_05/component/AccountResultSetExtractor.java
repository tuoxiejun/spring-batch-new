package com.example.chatprer11_05.component;

import com.example.chatprer11_05.domain.Account;
import com.example.chatprer11_05.domain.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountResultSetExtractor implements ResultSetExtractor<List<Account>> {
    private List<Account> accounts = new ArrayList<>();
    private Account currAccount;
    @Override
    public List<Account> extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()) {
            if (currAccount == null){
                currAccount = new Account(rs.getLong("account_id"),
                        rs.getBigDecimal("balance"), rs.getDate("last_statement_date")
                );
            }else if (rs.getLong("account_id") != currAccount.getId()){
                accounts.add(currAccount);
                currAccount = new Account(rs.getLong("account_id"), rs.getBigDecimal("balance"),
                        rs.getDate("last_statement_date")
                );
            }
            if (StringUtils.hasText(rs.getString("description"))) {
                currAccount.addTransaction(new Transaction(rs.getLong("transaction_id"),
                        rs.getLong("account_id"), rs.getString("description"),
                        rs.getBigDecimal("credit"), rs.getBigDecimal("debit"),
                        new Date(rs.getTimestamp("timestamp").getTime())
                ));
            }
        }
        if (currAccount != null){
            accounts.add(currAccount);
        }
        return accounts;
    }

}
