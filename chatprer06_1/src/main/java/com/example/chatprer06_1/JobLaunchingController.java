package com.example.chatprer06_1;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLaunchingController {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    ApplicationContext context;

    @Autowired
    JobExplorer jobExplorer;

    @PostMapping(path = "/run")
    public ExitStatus runJob(@RequestBody JobLaunchRequest request) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = context.getBean(request.getName(), Job.class);
        JobParameters jobParameters = new JobParametersBuilder(request.getJobParametersObject(), jobExplorer).getNextJobParameters(job).toJobParameters();

        return jobLauncher.run(job, jobParameters).getExitStatus();
    }
}
