package com.example.chatprer07_6;

import com.example.chatprer07_6.batch.*;
import com.example.chatprer07_6.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
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
    @StepScope
    public MultiResourceItemReader multiResourceItemReader(@Value("#{jobParameters['customerFile']}")Resource[] resources){
        return new MultiResourceItemReaderBuilder<Customer>()
                .name("multiResourceItemReader")
                .delegate(customerFileReader())
                .resources(resources)
                .build();
    }
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
    public FlatFileItemReader customerItemReader(){
        return new FlatFileItemReaderBuilder()
                .name("customerItemReader")
                .lineMapper(lineMapper())
                .build();
    }

    @Bean
    public CustomerFileReader customerFileReader(){
        return new CustomerFileReader(customerItemReader());
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
                .<Customer, Customer>chunk(2)
                .reader(multiResourceItemReader(null))
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public Job copyJob(){
        return jobBuilderFactory.get("copyJob")
//                .incrementer(new RunIdIncrementer())
                .start(copyStep())
                .build();
    }
}