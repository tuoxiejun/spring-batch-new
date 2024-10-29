package com.example.chatprer07_7.batch;

import org.springframework.batch.item.file.transform.*;

import java.util.ArrayList;

public class CustomerLineTokenizer extends DelimitedLineTokenizer {

    public CustomerLineTokenizer() {
        super();
        setNames(new String[] {"firstName", "middleInitial", "lastName", "address", "city", "state","zipCode"});
        setIncludedFields(1,2,3,4,5,6,7);
    }
}
//
//public class CustomerLineTokenizer implements LineTokenizer {
//
//    private String delimiter = ",";
//
//    private String[] names = new String[] {"firstName",
//            "middleInitial",
//            "lastName",
//            "address",
//            "city",
//            "state",
//            "zipCode"};
//
//    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
//
//    @Override
//    public FieldSet tokenize(String line) {
//        // 实现自定义的分词逻辑
//        String[] split = line.split(delimiter);
//        ArrayList<String> parsedFields = new ArrayList<>();
//        for (int i = 0; i < split.length; i++) {
//            if (i == 0){
//               continue;
//            } else {
//                parsedFields.add(split[i]);
//            }
//        }
//        return fieldSetFactory.create(parsedFields.toArray(new String[0]),names);
//    }
//}
