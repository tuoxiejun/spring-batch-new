package com.example.chatprer06_1;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Properties;

public class JobLaunchRequest {
    private String name;
    private Properties jobParameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Properties getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(Properties jobParameters) {
        this.jobParameters = jobParameters;
    }

    public JobParameters getJobParametersObject(){
        Properties properties = new Properties();
        properties.putAll(jobParameters);
        return new JobParametersBuilder(properties).toJobParameters();
    }
}
