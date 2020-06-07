package com.example.springbatch.multithread;

import com.example.springbatch.BaseJobConfiguration;
import com.example.springbatch.model.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/5/31 下午11:23
 */
@Configuration
public class MultiThreadJobConfiguration extends BaseJobConfiguration {

    public FlatFileItemReader<Transaction> fileTransactionReader() {
        Resource resource = new FileSystemResource("data/csv/bigtransactions.csv");
        return new FlatFileItemReaderBuilder<Transaction>()
                .saveState(false)
                .resource(resource)
                .delimited()
                .names(new String[]{"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> {
                    Transaction transaction = new Transaction();
                    transaction.setAccount(fieldSet.readString("account"));
                    transaction.setAmount(fieldSet.readBigDecimal("amount"));
                    transaction.setTimestamp(fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"));
                    return transaction;
                })
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Transaction> writer(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (:account, :amount, :timestamp)")
                .build();
    }

    @Bean("multithreadedJob")
    public Job multithreadedJob() {
        return this.jobs.get("multithreadedJob")
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.afterPropertiesSet();

        return this.steps.get("multithreadedStep")
                .<Transaction, Transaction>chunk(1000)
                .reader(fileTransactionReader())
                .writer(writer(null))
                .taskExecutor(taskExecutor)
                .build();
    }
}
