package com.example.chatprer07_3.batch;

import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.util.ArrayList;

public class CustomerLineTokenizer implements LineTokenizer {

    private String delimiter = ",";

    private String[] names = new String[] {"firstName",
            "middleInitial",
            "lastName",
            "address",
            "city",
            "state",
            "zipCode"};

    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();

    @Override
    public FieldSet tokenize(String line) {
        // 实现自定义的分词逻辑
        String[] split = line.split(delimiter);
        ArrayList<String> parsedFields = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (i == 4){
                parsedFields.set(i-1, parsedFields.get(i-1) + " " + split[i]);
            } else {
                parsedFields.add(split[i]);
            }
        }
        return fieldSetFactory.create(parsedFields.toArray(new String[0]),names);

    }
}
