package com.example.chatprer03_4;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;

public class LoggingStepStartStopListener2 implements StepExecutionListener, ChunkListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Before step: " + stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("After step: " + stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }

    @Override
    public void beforeChunk(ChunkContext context) {
        System.out.println("Before chunk: " + context.getStepContext().getStepName());
    }

    @Override
    public void afterChunk(ChunkContext context) {
        System.out.println("After chunk: " + context.getStepContext().getStepName());
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        System.out.println("After chunk error: " + context.getStepContext().getStepName());
    }
}