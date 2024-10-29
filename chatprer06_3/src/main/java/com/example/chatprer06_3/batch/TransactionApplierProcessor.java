package com.example.chatprer06_3.batch;

import com.example.chatprer06_3.domain.AccountSummary;
import com.example.chatprer06_3.domain.Transaction;
import com.example.chatprer06_3.domain.TransactionDao;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

public class TransactionApplierProcessor implements ItemProcessor<AccountSummary, AccountSummary> {

    private TransactionDao transactionDao;

    public TransactionApplierProcessor(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }


    @Override
    public AccountSummary process(AccountSummary item) throws Exception {
        List<Transaction> transactionsByAccountNumber = transactionDao.getTransactionsByAccountNumber(item.getAccountNumber());

        for (Transaction transaction : transactionsByAccountNumber) {
            item.setCurrentBalance(item.getCurrentBalance()+ transaction.getAmount());
        }
        return item;
    }
}
