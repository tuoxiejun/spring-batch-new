//package com.example.chatprer04_6;
//
//import com.example.chatprer04_6.decide.MyDecider;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.job.flow.JobExecutionDecider;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ConditionConfiguration2 {
//
//    @Autowired
//    JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    StepBuilderFactory stepBuilderFactory;
//
//
//    @Bean
//    public Job condistionJob() {
//        return jobBuilderFactory.get("condistionJob")
//                .start(firstStep())
//                .next(getDecider())
//                .from(getDecider())
//                .on("FAILED").to(failStep())
//                .from(getDecider()).on("*").to(successStep())
//                .end()
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    public JobExecutionDecider getDecider(){
//        return new MyDecider();
//    }
//
//    @Bean
//    public Tasklet passTasklet(){
//        return ((contribution, chunkContext) -> {
//            System.out.println("passTasklet");
////            throw new RuntimeException("passTasklet error");
//            return RepeatStatus.FINISHED;
//        });
//    }
//
//    @Bean
//    public Tasklet successTasklet(){
//        return ((contribution, chunkContext) -> {
//            System.out.println("successTasklet");
//            return RepeatStatus.FINISHED;});
//    }
//    @Bean
//    public Tasklet failTasklet(){
//        return ((contribution, chunkContext) -> {
//            System.out.println("failTasklet");
//            return RepeatStatus.FINISHED;});
//    }
//
//
//
//
//
//
//    @Bean
//    public Step firstStep(){
//        return stepBuilderFactory.get("firstStep")
//                .tasklet(passTasklet())
//                .build();
//    }
//    @Bean
//    public Step successStep(){
//        return stepBuilderFactory.get("successStep")
//                .tasklet(successTasklet())
//                .build();
//    }
//    @Bean
//    public Step failStep(){
//        return stepBuilderFactory.get("failStep")
//                .tasklet(failTasklet())
//                .build();
//    }
//
//
//}