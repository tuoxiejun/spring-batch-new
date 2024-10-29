package com.example.chatprer11_05;

import com.example.chatprer11_05.domain.Transaction;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@Profile("!master")
@EnableBatchIntegration
public class WorkerConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    private final RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
    public WorkerConfiguration(RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory){
        this.workerStepBuilderFactory = workerStepBuilderFactory;
    }

    @Bean
    public DirectChannel requests(){
        return new DirectChannel();
    }
    @Bean
    public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory){
        return IntegrationFlows
                .from(Amqp.inboundGateway(connectionFactory,"requests"))
                .channel(requests())
                .get();
    }
    @Bean
    public DirectChannel replies(){
        return new DirectChannel();
    }
    @Bean
    public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate){
        return IntegrationFlows.from(replies())
                .handle(Amqp.outboundAdapter(amqpTemplate)
                        .routingKey("replies"))
                .get();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<Transaction> transactionStaxEventItemReader(
            @Value("#{stepExecutionContext['file']}") Resource resource) {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Transaction.class);
        return new StaxEventItemReaderBuilder<Transaction>()
                .name("transactionStaxEventItemReader")
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(jaxb2Marshaller)
                .build();
    }

    @Bean
    ItemProcessor<Transaction,Transaction> transactionItemProcessor(){
        return item -> {
            System.out.println("-----------------processorstart  " + Thread.currentThread().getName());
            return item;
        };
    }
    @Bean
    public ItemWriter<Transaction> transactionItemWriter(){
        return items -> {
            System.out.println("-----------------writerstart  " + Thread.currentThread().getName());
        };
    }

    @Bean
    public Step workerStep(){
        return workerStepBuilderFactory.get("workerStep")
                .inputChannel(requests())
                .outputChannel(replies())
                .<Transaction, Transaction>chunk(10)
                .reader(transactionStaxEventItemReader(null))
                .processor(transactionItemProcessor())
                .writer(transactionItemWriter())
                .build();

    }
}
