package com.example.chatprer06_2;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {

    // Add job details and trigger here

    @Bean
    public JobDetail quartzJobDetail(){
        return JobBuilder.newJob(BatchScheduledJob.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger jobTrigger(){
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).withRepeatCount(4);
        return TriggerBuilder.newTrigger().forJob(quartzJobDetail()).withSchedule(simpleScheduleBuilder).build();
    }
}
