package com.example.chatprer05_2;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

public class MyTasklet implements Tasklet {
    JobExplorer jobExplorer;
    public MyTasklet(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String jobName = chunkContext.getStepContext().getJobName();

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);
        System.out.printf("Job %s has %d instances.",jobName,jobInstances.size());

        System.out.println("They have had the following results:");
        System.out.println("***********************************");
        for (JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
            System.out.printf("Job instance %s has %d executions", jobInstance.getInstanceId(), jobExecutions.size());
            for (JobExecution jobExecution : jobExecutions) {
                System.out.printf("Job execution %s has status %s", jobExecution.getId(), jobExecution.getStatus());
            }
        }
        // 在这里执行你的任务逻辑
        return RepeatStatus.FINISHED;
    }
}
