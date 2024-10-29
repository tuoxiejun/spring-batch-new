//package com.example.chatprer03_4;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.item.ExecutionContext;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Map;
//
//@Configuration
//public class JobConfig {
//
//    @Autowired
//    JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job job(){
//        return jobBuilderFactory.get("job1")
//                .start(step1())
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    public Step step1() {
//        return stepBuilderFactory.get("step1")
//                .tasklet(tasklet())
//                .build();
//    }
//
//    @Bean
//    public Tasklet tasklet() {
//        return (stepContribution, chunkContext) ->{
//            Map<String, Object> context1 = chunkContext.getStepContext().getJobExecutionContext();
//            System.out.println("start context1 = "+ context1);
//            Map<String, Object> context2 = chunkContext.getStepContext().getStepExecutionContext();
//            System.out.println("start context2 = "+ context2);
//
//            ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
//            stepExecutionContext.put("key1", "value1");
//            System.out.println("stepExecutionContext = "+ stepExecutionContext);
//
//            ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
//            jobExecutionContext.put("key2", "value2");
//            System.out.println("jobExecutionContext = "+ jobExecutionContext);
//
//            System.out.println("end context1 = "+ context1);
//            System.out.println("end context2 = "+ context2);
//
//            Map<String, Object> context3 = chunkContext.getStepContext().getJobExecutionContext();
//
//            System.out.println("end context3 = "+ context3);
//            Map<String, Object> context4 = chunkContext.getStepContext().getStepExecutionContext();
//            System.out.println("end context4 = "+ context4);
//
//            return RepeatStatus.FINISHED;
//        };
//    }
//}
