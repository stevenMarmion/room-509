package com.littlefish.app.repository;

import com.littlefish.app.model.DailyChallengeUser;
import com.littlefish.app.model.DailyChallengeUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyChallengeUserRepository extends JpaRepository<DailyChallengeUser, DailyChallengeUserId> {
    List<DailyChallengeUser> findByUserId(Long userId);
    void deleteByDailyChallengeId(Long dailyChallengeId);
}