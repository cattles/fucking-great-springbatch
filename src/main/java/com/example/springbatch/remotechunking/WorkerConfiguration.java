package com.example.springbatch.remotechunking;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import javax.annotation.Resource;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/6/20 下午5:22
 */
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@EnableIntegration
@PropertySource("classpath:remote-chunking.properties")
@Profile("worker")
public class WorkerConfiguration {
    @Value("${broker.url}")
    private String brokerUrl;

    @Resource
    private RemoteChunkingWorkerBuilder<Integer, Integer> remoteChunkingWorkerBuilder;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(this.brokerUrl);
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    /*
     * Configure inbound flow (requests coming from the master)
     */
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("requests"))
                .channel(requests())
                .get();
    }

    /*
     * Configure outbound flow (replies going to the master)
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(replies())
                .handle(Jms.outboundAdapter(connectionFactory).destination("replies"))
                .get();
    }

    /*
     * Configure worker components
     */
    @Bean
    public ItemProcessor<Integer, Integer> itemProcessor() {
        return item -> {
            System.out.println("processing item " + item);
            return item;
        };
    }

    @Bean
    public ItemWriter<Integer> itemWriter() {
        return items -> {
            for (Integer item : items) {
                System.out.println("writing item " + item);
            }
        };
    }

    @Bean
    public IntegrationFlow workerIntegrationFlow() {
        return this.remoteChunkingWorkerBuilder
                .itemProcessor(itemProcessor())
                .itemWriter(itemWriter())
                .inputChannel(requests())
                .outputChannel(replies())
                .build();
    }
}
