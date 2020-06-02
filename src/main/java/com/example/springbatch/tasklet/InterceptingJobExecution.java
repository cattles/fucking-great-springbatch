package com.example.springbatch.tasklet;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/6/2 下午5:00
 */
@Component
public class InterceptingJobExecution implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Intercepting Job Execution - Before Job!");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Intercepting Job Execution - after Job!");
    }
}
