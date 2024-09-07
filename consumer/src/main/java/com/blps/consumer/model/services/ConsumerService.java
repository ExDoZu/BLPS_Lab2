package com.blps.consumer.model.services;

import com.blps.common.UserHistoryDto;
import com.blps.common.ModerHistoryDto;
import com.blps.consumer.model.repository.logstats.UserHistoryRepository;
import com.blps.consumer.model.repository.logstats.ModerHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.blps.consumer.beans.UserHistory;
import com.blps.consumer.beans.ModerHistory;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ConsumerService {
    private final UserHistoryRepository userHistoryRepository;
    private final ModerHistoryRepository moderHistoryRepository;

    @KafkaListener(topics = "user_audit", groupId = "producer-1")
    public void listenUserAudit(ConsumerRecord<String, UserHistoryDto> record) {
        log.info("kafka [user_audit, producer-1]: {} - {}", record.key(), record.value());

        UserHistoryDto dto = record.value();

        UserHistory userHistory = convertToUserHistory(dto);
        userHistoryRepository.save(userHistory);
        log.info("User history saved: {}", userHistory);
    }

    @KafkaListener(topics = "moder_audit", groupId = "producer-1")
    public void listenModerAudit(ConsumerRecord<String, ModerHistoryDto> record) {
        log.info("kafka [moder_audit, producer-1]: {} - {}", record.key(), record.value());

        ModerHistoryDto dto = record.value();

        ModerHistory moderHistory = convertToModerHistory(dto);
        moderHistoryRepository.save(moderHistory);
        log.info("Moder history saved: {}", moderHistory);
    }

    @Scheduled(cron = "0 * * * * *")
    public void cleanOldUserHistory() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        log.info("Cleaning user history older than: {}", oneMinuteAgo);

        List<UserHistory> oldEntries = userHistoryRepository.findByDatetimeBefore(oneMinuteAgo);
        userHistoryRepository.deleteAll(oldEntries);

        log.info("Deleted {} old user history entries.", oldEntries.size());
    }

    private UserHistory convertToUserHistory(UserHistoryDto dto) {
        return new UserHistory(
                null,
                dto.getUserID(),
                UserHistory.UserAction.valueOf(dto.getAction().name()),
                dto.getInteracted_post(),
                dto.getNote(),
                dto.getDatetime()
        );
    }

    private ModerHistory convertToModerHistory(ModerHistoryDto dto) {
        return new ModerHistory(
                null,
                dto.getModerID(),
                ModerHistory.ModerAction.valueOf(dto.getAction().name()),
                dto.getInteracted_post(),
                dto.getNote(),
                dto.getDatetime()
        );
    }
}
