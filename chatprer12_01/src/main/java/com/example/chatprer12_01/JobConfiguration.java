package com.example.chatprer12_01;

import com.example.chatprer12_01.component.DownloadingJobExecutionListener;
import com.example.chatprer12_01.component.EnrichmentProcessor;
import com.example.chatprer12_01.domain.Foo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JobConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    DownloadingJobExecutionListener downloadingJobExecutionListener;


    @Bean
    @StepScope
    public MultiResourceItemReader reader(
            @Value("#{jobExecutionContext['localFiles']}") String paths
    ) throws IOException {
        System.out.println(">> paths : " + paths);
        MultiResourceItemReader<Foo> multiResourceItemReader = new MultiResourceItemReader<>();
        multiResourceItemReader.setName("multiResourceItemReader");
        multiResourceItemReader.setDelegate(delegate());

        String[] split = paths.split(",");
        System.out.println(">> pathsLength: " + split.length);

        List<Resource> resources = new ArrayList<>(split.length);
        for (String s : split) {
            FileSystemResource resource = new FileSystemResource(s);
            System.out.println(">> resource = " + resource.getURI());
            resources.add(resource);
        }
        multiResourceItemReader.setResources(resources.toArray(new Resource[0]));
        return multiResourceItemReader;
    }

    @Bean
    public FlatFileItemReader<Foo> delegate(){
        return new FlatFileItemReaderBuilder<Foo>()
                .name("delegate")
                .delimited()
                .names("first","second","third")
                .targetType(Foo.class)
                .build();
    }

    @Bean
    @StepScope
    public EnrichmentProcessor processor(){
        return new EnrichmentProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Foo> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Foo>()
                .dataSource(dataSource)
                .sql("insert into foo(first,second,third,message) values (:first,:second,:third,:message)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step load() throws IOException {
        return this.stepBuilderFactory.get("load")
                .<Foo,Foo>chunk(10)
                .reader(reader(null))
                .processor(processor())
                .writer(writer(null))
                .build();
    }

    @Bean
    public Job job() throws IOException {
        return this.jobBuilderFactory.get("s3jdbc")
                .start(load())
                .listener(downloadingJobExecutionListener)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
