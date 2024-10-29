package com.example.chatprer07_7.domain;

import lombok.Data;

import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@Data
@XmlType(name = "transaction")
public class Transaction {

    private String accountNumber;
    private Date transactionDate;
    private Double amount;
}
