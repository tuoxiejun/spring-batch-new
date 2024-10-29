package com.example.chatprer09_14;

import com.example.chatprer09_14.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.batch.item.jms.builder.JmsItemReaderBuilder;
import org.springframework.batch.item.jms.builder.JmsItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;

@Configuration
public class HibernateImportJob {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    public HibernateImportJob(JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFileReader(
            @Value("#{jobParameters['customerFile']}")Resource inputFile) {

        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[] {"firstName",
                        "middleInitial",
                        "lastName",
                        "address",
                        "city",
                        "state",
                        "zip"})
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public JmsItemWriter<Customer> jmsItemWriter(JmsTemplate jmsTemplate){
        return new JmsItemWriterBuilder<Customer>()
                .jmsTemplate(jmsTemplate)
                .build();
    }


    @Bean
    public JmsItemReader<Customer> customerJmsReader(JmsTemplate jmsTemplate){
        return new JmsItemReaderBuilder<Customer>()
                .jmsTemplate(jmsTemplate)
                .itemType(Customer.class)
                .build();
    }

    @Bean
    public StaxEventItemWriter<Customer> xmlOutputWriter() {

        HashMap<String, Class> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(aliases);


        return new StaxEventItemWriterBuilder<Customer>()
                .name("xmlOutputWriter")
                .marshaller(marshaller)
                .rootTagName("customers")
                .resource(new FileSystemResource("input/output.xml"))
                .build();
    }



    @Bean
    public Step formatInputStep(){
        return stepBuilderFactory.get("formatInputStep")
                .<Customer,Customer> chunk(10)
                .reader(customerFileReader(null))
                .writer(jmsItemWriter(null))
                .build();
    }

    @Bean
    public Step formatOutputStep(){
        return stepBuilderFactory.get("formatOutputStep")
                .<Customer,Customer> chunk(10)
                .reader(customerJmsReader(null))
                .writer(xmlOutputWriter())
                .build();
    }

    @Bean
    public Job hibernateFormatJob() throws Exception {
        return this.jobBuilderFactory.get("hibernateFormatJob")
                .incrementer(new RunIdIncrementer())
                .start(formatInputStep())
                .next(formatOutputStep())
                .build();
    }
}