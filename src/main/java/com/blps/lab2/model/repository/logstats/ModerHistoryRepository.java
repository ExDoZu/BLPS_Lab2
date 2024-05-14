package com.blps.lab2.model.repository.logstats;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.blps.lab2.model.beans.logstats.ModerHistory;

@Repository
public interface ModerHistoryRepository extends JpaRepository<ModerHistory, Long> {

}
