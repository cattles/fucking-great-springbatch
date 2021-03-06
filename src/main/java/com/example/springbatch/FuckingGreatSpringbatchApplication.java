package com.example.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 稻草鸟人
 * @date 20200602
 */
@SpringBootApplication
@EnableBatchProcessing
@EnableBatchIntegration
public class FuckingGreatSpringbatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuckingGreatSpringbatchApplication.class, args);
    }

}
