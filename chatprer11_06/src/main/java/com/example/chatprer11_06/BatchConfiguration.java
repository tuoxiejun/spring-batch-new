package com.example.chatprer11_06;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.batch.partition.DeployerPartitionHandler;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.cloud.task.batch.partition.PassThroughCommandLineArgsProvider;
import org.springframework.cloud.task.batch.partition.SimpleEnvironmentVariablesProvider;
import org.springframework.cloud.task.repository.TaskRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BatchConfiguration {
    @Autowired
    private  JobBuilderFactory jobBuilderFactory;
    @Autowired
    private  StepBuilderFactory stepBuilderFactory;
    @Autowired
    private  JobRepository jobRepository;
    @Autowired
    private  ConfigurableApplicationContext context;

    @Bean
    @Profile("master")
    public DeployerPartitionHandler partitionHandler(TaskLauncher taskLauncher,
                                                     JobExplorer jobExplorer,
                                                     ApplicationContext context,
                                                     Environment environment){
        Resource resource = context.getResource("");
        DeployerPartitionHandler partitionHandler = new DeployerPartitionHandler(taskLauncher, jobExplorer, resource, "step1");
        List<String> commandLineArgs = new ArrayList<>(3);
        commandLineArgs.add("--spring.profiles.active=worker");
        commandLineArgs.add("--spring.cloud.task.initialize.enable=false");
        commandLineArgs.add("--spring.batch.initializer.enabled=false");
        commandLineArgs.add("--spring.datasource.initialize=false");
        partitionHandler.setCommandLineArgsProvider(new PassThroughCommandLineArgsProvider(commandLineArgs));
        partitionHandler.setEnvironmentVariablesProvider(new SimpleEnvironmentVariablesProvider(environment));
        partitionHandler.setMaxWorkers(3);
        partitionHandler.setApplicationName("PartitionedBatchJobTask");
        return partitionHandler;
    }

    @Bean
    @Profile("worker")
    public DeployerStepExecutionHandler stepExecutionHandler(JobExplorer jobExplorer){
        return new DeployerStepExecutionHandler(this.context,jobExplorer,this.jobRepository);
    }

}
