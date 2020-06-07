package com.example.springbatch.tasklet;

import com.example.springbatch.BaseJobConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/5/31 下午11:23
 */
@Configuration
public class TaskletJobConfiguration extends BaseJobConfiguration {


    @Autowired
    InterceptingJobExecution interceptingJobExecution;

    @Bean(name = "taskletJob")
    public Job taskletJob() {
        return this.jobs.get("taskletJob")
                .start(step()).listener(interceptingJobExecution)
                .build();
    }

    @Bean
    protected Step step() {
        return steps
                .get("step")
                .tasklet(messageTasklet())
                .build();
    }

    @Bean
    public MessageTasklet messageTasklet() {
        MessageTasklet tasklet = new MessageTasklet();
        return tasklet;
    }

}
