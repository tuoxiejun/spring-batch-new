package com.example.chatprer11_07;

import com.example.chatprer11_07.domain.Transaction;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.RemoteChunkingMasterStepBuilderFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableBatchIntegration
public class BatchConfiguration {
//    @Bean
//    public MessageConverter jsonMessageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }
    @Configuration
    @Profile("!worker")
    public static class MasterConfiguration{

        @Autowired
        private  JobBuilderFactory jobBuilderFactory;
        @Autowired
        private RemoteChunkingMasterStepBuilderFactory remoteChunkingMasterStepBuilderFactory;

        @Bean
        public DirectChannel requests(){
            return new DirectChannel();
        }
        @Bean
        public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate){
            return IntegrationFlows.from(requests())
                    .handle(Amqp.outboundAdapter(amqpTemplate)
                            .routingKey("requests"))
                    .get();
        }

        @Bean
        public QueueChannel replies(){
            return new QueueChannel();
        }
        @Bean
        public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory){
            return IntegrationFlows
                    .from(Amqp.inboundAdapter(connectionFactory, "replies"))
                    .channel(replies())
                    .get();
        }

        @Bean
        @StepScope
        public StaxEventItemReader<Transaction> transactionStaxEventItemReader(
                @Value("#{jobParameters['inputFiles']}") Resource resource){
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
        public TaskletStep masterStep(){
            return this.remoteChunkingMasterStepBuilderFactory.get("masterStep")
                    .chunk(100)
                    .reader(transactionStaxEventItemReader(null))
                    .outputChannel(requests())
                    .inputChannel(replies())
                    .build();
        }
        @Bean
        public Job remoteChunkingJob(){
            return this.jobBuilderFactory.get("remoteChunkingJob")
                    .start(masterStep())
                    .incrementer(new RunIdIncrementer())
                    .build();
        }
    }

    @Configuration
    @Profile("worker")
    public static class WorkerConfiguration{
        @Autowired
        private RemoteChunkingWorkerBuilder<Transaction,Transaction> workerBuilder;


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
        public ItemProcessor<Transaction,Transaction> processor(){
            return item -> {
                System.out.println("Processing item: " + item);
                return item;
            };
        }

        @Bean
        public ItemWriter<Transaction> writer(){
            return items -> {
                for (Transaction item : items)
                    System.out.println("Writing item: " + item);
            };
        }

        @Bean
        public IntegrationFlow integrationFlow(){
            return this.workerBuilder
                    .itemProcessor(processor())
                    .itemWriter(writer())
                    .inputChannel(requests())
                    .outputChannel(replies())
                    .build();
        }

    }

}
