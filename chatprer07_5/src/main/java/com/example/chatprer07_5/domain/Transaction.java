package com.example.chatprer07_5.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Transaction {

    private String accountNumber;
    private Date transactionDate;
    private Double amount;
}
