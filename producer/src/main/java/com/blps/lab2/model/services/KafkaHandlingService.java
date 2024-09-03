package com.blps.lab2.model.services;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

import com.blps.lab2.config.kafka.*;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaHandlingService {

    private final Producer<String, String> kafkaProducer;
    private final Admin admin;
    private final List<NewTopic> topics;

    @EventListener(ContextRefreshedEvent.class)
    private void createTopics() {
        try {
            log.info("Starting topic creation process.");
            CreateTopicsResult result = admin.createTopics(topics);
            for (Map.Entry<String, KafkaFuture<Void>> entry : result.values().entrySet()) {
                try {
                    entry.getValue().get();
                    log.info("Successfully created topic: {}", entry.getKey());
                } catch (ExecutionException | InterruptedException excp) {
                    log.warn("Failed to create topic {}: {}", entry.getKey(), excp.getMessage());
                }
            }
        } catch (RuntimeException excp) {
            log.error("Topic creation process failed: {}", excp.getMessage(), excp);
        }
    }

    public void send(String topicName, String key, String value) {
        try {
            kafkaProducer.send(new ProducerRecord<>(topicName, key, value));
            log.info("Message sent to topic '{}': key = '{}', value = '{}'", topicName, key, value);
        } catch (Exception excp) {
            log.error("Failed to send message to topic '{}': {}", topicName, excp.getMessage(), excp);
        } finally {
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }
}
