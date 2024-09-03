package com.blps.consumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConsumerService {
    @KafkaListener(topics = "user", groupId = "producer-1")
    public void listenUser(ConsumerRecord<String, String> record) {
        log.info("kafka [user, producer-1]: {} - {}", record.key(), record.value());
    }
}