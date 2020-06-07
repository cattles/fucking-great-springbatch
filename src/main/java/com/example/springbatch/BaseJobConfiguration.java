package com.example.springbatch;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/6/5 下午10:04
 */
public class BaseJobConfiguration {

    @Autowired
    protected JobBuilderFactory jobs;

    @Autowired
    protected StepBuilderFactory steps;
}
