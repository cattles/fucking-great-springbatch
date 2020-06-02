package com.example.springbatch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/5/31 下午10:56
 */
public class MessageTasklet implements Tasklet {


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String message = (String) chunkContext.getStepContext().getJobParameters().get("message");
        ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

        jobContext.put("message", message);

        System.out.println(message);

        return RepeatStatus.FINISHED;
    }
}
