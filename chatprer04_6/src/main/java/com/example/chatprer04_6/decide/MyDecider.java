package com.example.chatprer04_6.decide;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Random;

public class MyDecider implements JobExecutionDecider {

    Random random = new Random();
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if (random.nextBoolean()){
            return FlowExecutionStatus.FAILED;
        }else {
            return FlowExecutionStatus.COMPLETED;
        }
    }
}
