package com.example.chatprer12_02.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "transaction")
public class Transaction implements Serializable {
    private long transactionId;
    private long accountId;
    private String description;
    private BigDecimal credit;
    private BigDecimal debit;
//    private Date timestamp;
//    @XmlJavaTypeAdapter(JaxbDateSerializer.class)
//    public void setTimestamp(Date timestamp) {
//        this.timestamp = timestamp;
//    }
//    public BigDecimal getTransactionAmount() {
//        if(credit != null) {
//            if(debit != null) {
//                return credit.add(debit);
//            }
//            else {
//                return credit;
//            }
//        }
//        else if(debit != null) {
//            return debit;
//        }
//        else {
//            return new BigDecimal(0);
//        }
//    }
}
