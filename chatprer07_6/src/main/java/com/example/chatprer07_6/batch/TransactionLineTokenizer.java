package com.example.chatprer07_6.batch;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class TransactionLineTokenizer extends DelimitedLineTokenizer {

    public TransactionLineTokenizer() {
        super();
        super.names = new String[] {"prefix", "accountNumber", "transactionDate", "amount"};
    }
}
