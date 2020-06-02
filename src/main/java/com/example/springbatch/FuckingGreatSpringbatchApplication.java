package com.example.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @date 20200602
 * @author 稻草鸟人
 */
@SpringBootApplication
@EnableBatchProcessing
public class FuckingGreatSpringbatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuckingGreatSpringbatchApplication.class, args);
    }

}
