package com.example.chatprer09_16.component;

import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.stereotype.Component;

@Component
public class CustomerOutputFileSuffixCreator implements ResourceSuffixCreator {
    @Override
    public String getSuffix(int index) {
        return "_" + index + ".xml";
    }
}
