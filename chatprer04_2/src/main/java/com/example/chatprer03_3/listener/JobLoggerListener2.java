package com.example.chatprer03_3.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class JobLoggerListener2 {
   @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        System.out.println(" JobLoggerListener2  Job " + jobName + " is starting...");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        ExitStatus exitStatus = jobExecution.getExitStatus();
        System.out.println("JobLoggerListener2 Job " + jobExecution.getJobInstance().getJobName() + " finished with status: " + exitStatus);
    }
}
