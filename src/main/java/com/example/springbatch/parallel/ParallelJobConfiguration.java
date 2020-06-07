package com.example.springbatch.parallel;

import com.example.springbatch.BaseJobConfiguration;
import com.example.springbatch.model.Transaction;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/5/31 下午11:23
 */
@Configuration
public class ParallelJobConfiguration extends BaseJobConfiguration {

    @Bean
    @StepScope
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
    public StaxEventItemReader<Transaction> xmlTransactionReader() {
        Resource resource = new FileSystemResource("data/xml/bigtransactions.xml");
        Map<String, Class> map = new HashMap<>();
        map.put("transaction", Transaction.class);
        map.put("account", String.class);
        map.put("amount", BigDecimal.class);
        map.put("timestamp", Date.class);
        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(map);
        String[] formats = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};
        marshaller.setConverters(new DateConverter("yyyy-MM-dd HH:mm:ss", formats));

        return new StaxEventItemReaderBuilder<Transaction>()
                .name("xmlFileTransactionReader")
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(marshaller)
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Transaction> jdbcBatchItemWriter(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (:account, :amount, :timestamp)")
                .build();
    }


    @Bean("parallelJob")
    public Job parallelStepsJob() {

        return this.jobs.get("parallelJob")
                .start(parallelFlow())
                .end()
                .build();
    }

    @Bean
    public Flow parallelFlow() {
        return new FlowBuilder<Flow>("parallelFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1(), flow2())
                .build();
    }

    @Bean
    public Flow flow1() {
        return new FlowBuilder<Flow>("flow1")
                .start(step1())
                .build();
    }

    @Bean
    public Flow flow2() {
        return new FlowBuilder<Flow>("flow2")
                .start(step2())
                .build();
    }

    @Bean("xmlStep")
    public Step step1() {
        return this.steps.get("xmlStep")
                .<Transaction, Transaction>chunk(1000)
                .reader(xmlTransactionReader())
                .writer(jdbcBatchItemWriter(null))
                .build();
    }

    @Bean("fileStep")
    public Step step2() {
        return this.steps.get("fileStep")
                .<Transaction, Transaction>chunk(1000)
                .reader(fileTransactionReader())
                .writer(jdbcBatchItemWriter(null))
                .build();
    }


}
