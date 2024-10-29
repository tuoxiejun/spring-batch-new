package com.example.chatprer03_2.paraminc;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;

@Configuration
public class ParamInc {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;


    public JobParametersValidator fileNameEndCheck(){
        return parameters -> {
            String fileName = parameters.getString("fileName");
            if (!StringUtils.hasText(fileName)){
                throw new JobParametersInvalidException("File name must not be empty");
            }
            if(!fileName.endsWith(".txt")){
                throw new JobParametersInvalidException("File name must end with .txt");
            };
        };
    }


    @Bean
    public JobParametersValidator parametersValidator(){
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
        defaultJobParametersValidator.setRequiredKeys(new String[]{"fileName"});
        defaultJobParametersValidator.setOptionalKeys(new String[]{"name","run.id"});
//        defaultJobParametersValidator.setOptionalKeys(new String[]{"name","currentDate"});
        defaultJobParametersValidator.afterPropertiesSet();

        validator.setValidators(Arrays.asList( defaultJobParametersValidator,fileNameEndCheck()));

        return validator;
    }

    public JobParametersIncrementer currentDateJobParametersIncrementer() {
        return parameters -> new JobParametersBuilder(parameters)
                .addDate("currentDate", new Date())
                .toJobParameters();
    }

    @Bean
    public Job paramCheckJob(Step paramCheckStep) {
        return jobBuilderFactory.get("paramCheckJob")
                .start(paramCheckStep)
                .validator(parametersValidator())
                .incrementer(new RunIdIncrementer())
//                .incrementer(currentDateJobParametersIncrementer())
                .build();
    }

    @Bean
    public Step  paramCheckStep() {
        return stepBuilderFactory.get("paramCheckStep")
                .tasklet(paramCheckTasklet(null,null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet paramCheckTasklet(@Value("#{jobParameters['fileName']}") String fileName,
                                     @Value("#{jobParameters['name']}") String name) {
        return (stepContribution, chunkContext) ->{
            System.out.printf(" fileName: %s, name: %s%n", fileName, name);
            return RepeatStatus.FINISHED;
        };
    }
}
