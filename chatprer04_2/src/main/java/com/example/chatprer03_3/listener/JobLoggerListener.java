package com.example.chatprer03_3.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobLoggerListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        System.out.println("Job " + jobName + " is starting...");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        ExitStatus exitStatus = jobExecution.getExitStatus();
        System.out.println("Job " + jobExecution.getJobInstance().getJobName() + " finished with status: " + exitStatus);
    }
}
