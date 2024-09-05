package com.blps.consumer.model.services;

import com.blps.common.UserHistoryDto;
import com.blps.common.ModerHistoryDto;
import com.blps.consumer.model.repository.logstats.UserHistoryRepository;
import com.blps.consumer.model.repository.logstats.ModerHistoryRepository;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.blps.consumer.beans.UserHistory;
import com.blps.consumer.beans.ModerHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ConsumerService {
    private final UserHistoryRepository userHistoryRepository;
    private final ModerHistoryRepository moderHistoryRepository;

    @KafkaListener(topics = "user_audit", groupId = "producer-1")
    public void listenUserAudit(ConsumerRecord<String, UserHistoryDto> record) {
        log.info("kafka [user_audit, producer-1]: {} - {}", record.key(), record.value());

        UserHistoryDto dto = record.value();

        UserHistory userHistory = new UserHistory(
                null,
                dto.getUserID(),
                UserHistory.UserAction.valueOf(dto.getAction().name()),
                dto.getInteracted_post(),
                dto.getNote(),
                dto.getDatetime()
        );
        userHistoryRepository.save(userHistory);
    }

    @KafkaListener(topics = "moder_audit", groupId = "producer-1")
    public void listenModerAudit(ConsumerRecord<String, ModerHistoryDto> record) {
        log.info("kafka [moder_audit, producer-1]: {} - {}", record.key(), record.value());

        ModerHistoryDto dto = record.value();

        ModerHistory moderHistory = new ModerHistory(
                null,
                dto.getModerID(),
                ModerHistory.ModerAction.valueOf(dto.getAction().name()),
                dto.getInteracted_post(),
                dto.getNote(),
                dto.getDatetime()
        );
        moderHistoryRepository.save(moderHistory);
    }

    @Scheduled(cron = "0 * * * * *")
    public void cleanOldUserHistory() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        log.info("Cleaning user history older than: {}", oneMinuteAgo);

        List<UserHistory> oldEntries = userHistoryRepository.findByDatetimeBefore(oneMinuteAgo);
        userHistoryRepository.deleteAll(oldEntries);

        log.info("Deleted {} old user history entries.", oldEntries.size());
    }
}
