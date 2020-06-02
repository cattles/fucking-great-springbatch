package com.example.springbatch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/6/1 下午12:54
 */
@RestController
@Slf4j
public class JobLauncherController {


    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @RequestMapping("/launchjob")
    public String handle() throws Exception {
        String parameter = UUID.randomUUID().toString();
        try {
            JobParameters jobParameters = new JobParametersBuilder().addString("message", "Welcome To Spring Batch World!" + parameter)
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.error("", e);
        }

        return parameter;
    }
}
