package com.example.chatprer03_4;

import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.DefaultResultCompletionPolicy;

import java.util.Random;

public class RandomCompletionPolicy extends DefaultResultCompletionPolicy {

    int chunkSize;

    int totalItemNum;

    Random random = new Random();

    @Override
    public RepeatContext start(RepeatContext context) {
        chunkSize = random.nextInt(20);
        totalItemNum = 0;
        return context;
    }

    @Override
    public void update(RepeatContext context) {
        totalItemNum++;
    }

    @Override
    public boolean isComplete(RepeatContext context) {
        return totalItemNum >= chunkSize;
    }

    @Override
    public boolean isComplete(RepeatContext context, RepeatStatus result) {
        return super.isComplete(context, result) || isComplete(context);
    }
}
