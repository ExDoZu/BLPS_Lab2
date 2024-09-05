package com.blps.consumer.model.repository.logstats;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.blps.consumer.beans.ModerHistory;

@Repository
public interface ModerHistoryRepository extends JpaRepository<ModerHistory, Long> {

}
