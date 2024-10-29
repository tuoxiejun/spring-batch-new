package com.example.chatprer03_4;

import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;

public class LoggingStepStartStopListener1 {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Before step: " + stepExecution.getStepName());
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("After step: " + stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        System.out.println("Before chunk: " + context.getStepContext().getStepName());
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        System.out.println("After chunk: " + context.getStepContext().getStepName());
    }
}