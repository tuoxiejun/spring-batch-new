package com.example.chatprer09_18;

import com.example.chatprer09_18.domain.Customer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Component
@Aspect
public class CustomFooterCallback implements FlatFileFooterCallback {
    private int count = 0;
    @Override
    public void writeFooter(Writer writer) throws IOException {
        writer.write("total: " + count);
    }
    @Before("execution(* org.springframework.batch.item.file.FlatFileItemWriter.open(..))")
    public void resetCounter(){
        this.count = 0;
    }
    @Before("execution(* org.springframework.batch.item.file.FlatFileItemWriter.write(..))")
    public void beforeWriter(JoinPoint joinPoint){
        List<Customer> items = (List<Customer>) joinPoint.getArgs()[0];
        this.count += items.size();
    }
}
