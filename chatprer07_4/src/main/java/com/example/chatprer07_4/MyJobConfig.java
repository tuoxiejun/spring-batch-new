package com.example.chatprer07_4;

import com.example.chatprer07_4.batch.CustomerFieldSetMapper;
import com.example.chatprer07_4.batch.CustomerLineTokenizer;
import com.example.chatprer07_4.batch.TransactionFieldSetMapper;
import com.example.chatprer07_4.batch.TransactionLineTokenizer;
import com.example.chatprer07_4.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.batch.api.chunk.ItemReader;
import java.util.HashMap;

@Configuration
public class MyJobConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public PatternMatchingCompositeLineMapper lineMapper(){
        HashMap<String, LineTokenizer> lineTokenizerHashMap = new HashMap<>(2);
        lineTokenizerHashMap.put("CUST*", new CustomerLineTokenizer());
        lineTokenizerHashMap.put("TRANS*", new TransactionLineTokenizer());

        HashMap<String, FieldSetMapper> fieldSetMapperHashMap = new HashMap<>(2);
        fieldSetMapperHashMap.put("CUST*", new CustomerFieldSetMapper());
        fieldSetMapperHashMap.put("TRANS*", new TransactionFieldSetMapper());

        PatternMatchingCompositeLineMapper patternMatchingCompositeLineMapper = new PatternMatchingCompositeLineMapper<>();
        patternMatchingCompositeLineMapper.setFieldSetMappers(fieldSetMapperHashMap);
        patternMatchingCompositeLineMapper.setTokenizers(lineTokenizerHashMap);

        return patternMatchingCompositeLineMapper;
    }

    @Bean
    @StepScope
    public FlatFileItemReader customerItemReader(@Value("#{jobParameters['inputFile']}")Resource resource){
        return new FlatFileItemReaderBuilder().name("customerItemReader")
                .lineMapper(lineMapper())
                .resource(resource)
                .build();
    }

    @Bean
    public ItemWriter customerItemWriter(){
        return items -> {
            items.forEach(System.out::println);
        };
    }

    @Bean
    public Step copyStep(){
        return stepBuilderFactory.get("copyStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null))
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public Job copyJob(){
        return jobBuilderFactory.get("copyJob")
                .incrementer(new RunIdIncrementer())
                .start(copyStep())
                .build();
    }
}