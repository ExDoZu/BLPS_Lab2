package com.blps.lab2.model.repository.logstats;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.blps.lab2.model.beans.logstats.UserHistory;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

}
