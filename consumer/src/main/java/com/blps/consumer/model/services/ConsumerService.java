package com.blps.consumer.model.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.blps.consumer.model.beans.logstats.UserHistory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConsumerService {
    @KafkaListener(topics = "user", groupId = "producer-1")
    public void listenUser(ConsumerRecord<String, String> record) {
        log.info("kafka [user, producer-1]: {} - {}", record.key(), record.value());
    }

    @KafkaListener(topics = "user_audit", groupId = "producer-1")
    public void listenUserAudit(ConsumerRecord<String, UserHistory> record) {
        log.info("kafka [user_audit, producer-1]: {} - {}", record.key(), record.value());
    }

}