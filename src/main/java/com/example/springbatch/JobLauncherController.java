package com.example.springbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @Resource(name = "taskletJob")
    Job job;

    @Resource(name = "multithreadedJob")
    Job multithreadedJob;

    @Resource(name = "parallelJob")
    Job parallelJob;

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

    @RequestMapping("/launchMultiThreadjob")
    public String launchMultiThreadjob() throws Exception {
        String parameter = UUID.randomUUID().toString();
        try {
            jobLauncher.run(multithreadedJob, new JobParameters());
        } catch (Exception e) {
            log.error("", e);
        }

        return parameter;
    }

    @RequestMapping("/launchParallelJobjob")
    public String launchParallelJobjob() throws Exception {
        String parameter = UUID.randomUUID().toString();
        try {
            jobLauncher.run(parallelJob, new JobParameters());
        } catch (Exception e) {
            log.error("", e);
        }

        return parameter;
    }
}
