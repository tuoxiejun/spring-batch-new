package com.example.chatprer11_04.component;

import com.example.chatprer11_04.domain.Account;
import com.example.chatprer11_04.domain.Statement;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class AccountItemProcessor implements ItemProcessor<Statement, Statement> {
    private JdbcTemplate jdbcTemplate;
    public AccountItemProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Statement process(Statement item) throws Exception{

//        int threadCount = 10;
//
//        CountDownLatch doneSignal = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            Thread thread = new Thread(() -> {
//                for (int i1 = 0; i1 < 1000000; i1++) {
////                    System.out.println(Thread.currentThread().getName());
//                    new BigInteger(String.valueOf(i1)).isProbablePrime(0);
//                }
//                doneSignal.countDown();
//            });
//            thread.start();
//        }
//
//        doneSignal.await();

//        String memoryBuster = "memoryBuster";
//
//        for (int i = 0; i < 200; i++) {
//            System.out.println(1);
//            memoryBuster += memoryBuster;
//        }

        List<Account> accounts = jdbcTemplate.query("select a.account_id," +
                "       a.balance," +
                "       a.last_statement_date," +
                "       t.transaction_id," +
                "       t.description," +
                "       t.credit," +
                "       t.debit," +
                "       t.timestamp " +
//                "from account a left join " +  //HSQLDB
//                "    transaction t on a.account_id = t.account_account_id " +
				"from account a left join " +  //MYSQL
				"    transaction t on a.account_id = t.account_account_id " +
                "where a.account_id in " +
                "	(select account_account_id " +
                "	from customer_account " +
                "	where customer_customer_id = ?) " +
                "order by t.timestamp", new Object[]{item.getCustomer().getId()}, new AccountResultSetExtractor());
        System.out.println("AccountItemProcessor.process: " + accounts);
        item.setAccounts(accounts);
        return item;
    }
}
