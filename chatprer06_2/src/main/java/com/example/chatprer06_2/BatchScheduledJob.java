package com.example.chatprer06_2;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduledJob extends QuartzJobBean {

    @Autowired
    Job job;

    @Autowired
    JobExplorer jobExplorer;

    @Autowired
    JobLauncher jobLauncher;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer).getNextJobParameters(job).toJobParameters();

        try {
            jobLauncher.run(job,jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }
}
