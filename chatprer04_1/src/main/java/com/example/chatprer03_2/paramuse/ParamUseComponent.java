//package com.example.chatprer03_2.paramuse;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import java.util.Date;
//import java.util.Map;
//
//@Configuration
//public class ParamUseComponent {
//
//
//    @Autowired
//    JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Step step1(){
//        return stepBuilderFactory.get("step1")
//                .tasklet((contribution, chunkContext) -> {
//                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//                    System.out.println("chunkContext中获取参数开始");
//                    System.out.println("jobParameters: " + jobParameters);
//                    System.out.println("batchDate: "+ jobParameters.get("batchDate"));
//                    System.out.println("longValue: "+ jobParameters.get("longKey"));
//                    System.out.println("chunkContext中获取参数结束");
//                    return RepeatStatus.FINISHED;
//                }).build();
//    }
//    @Bean
//    public Job job(){
//        return jobBuilderFactory.get("job")
//                .start(step1())
//                .next(step2())
//                .build();
//    }
//
//    /**
//     * 通过ChunkContext方式获取参数
//     * @return
//     */
//
//
//    @Bean
//    public Step step2(){
//        return stepBuilderFactory.get("step2")
//                .tasklet(tasklet1(null,null))
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public Tasklet tasklet1(
//            @Value("#{jobParameters['batchDate']}") Date batchDate,
//            @Value("#{jobParameters['longKey']}")Long longValue){
//        return  (contribution, chunkContext) -> {
//            System.out.println("延迟绑定获取参数开始");
//            System.out.println("batchDate: "+ batchDate);
//            System.out.println("longValue: "+ longValue);
//            System.out.println("延迟绑定获取参数结束");
//            return RepeatStatus.FINISHED;
//        };
//    }
//}
