package com.littlefish.app.repository;

import com.littlefish.app.model.DailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyChallengeRepository extends JpaRepository<DailyChallenge, Long> {
}
