package com.blps.lab2.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTopic {

    @Value("${spring.kafka.topic.user-service-topic.topic-name}")
    private String topicName;
    @Value("${spring.kafka.topic.user-service-topic.partitions}")
    private int partitions;
    @Value("${spring.kafka.topic.user-service-topic.replication-factor}")
    private short replicationFactor;

    @Bean
    public NewTopic topic() {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

}