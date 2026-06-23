package com.littlefish.app.service;

import com.littlefish.app.model.DailyChallenge;
import com.littlefish.app.model.DailyChallengeUser;
import com.littlefish.app.model.DailyChallengeUserId;
import com.littlefish.app.model.User;
import com.littlefish.app.repository.DailyChallengeRepository;
import com.littlefish.app.repository.DailyChallengeUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyChallengeService {

    private final DailyChallengeRepository dailyChallengeRepository;
    private final DailyChallengeUserRepository dailyChallengeUserRepository;

    public List<DailyChallenge> findAll() {
        return dailyChallengeRepository.findAll();
    }

    public Optional<DailyChallenge> findById(Long id) {
        return dailyChallengeRepository.findById(id);
    }

    public List<DailyChallenge> findRandom(int count) {
        List<DailyChallenge> allChallenges = dailyChallengeRepository.findAll();
        if (allChallenges.size() <= count) {
            return allChallenges;
        } else {
            Collections.shuffle(allChallenges);
            return allChallenges.subList(0, count);
        }
    }

    @Transactional
    public DailyChallenge save(DailyChallenge challenge) {
        return dailyChallengeRepository.save(challenge);
    }

    @Transactional
    public Optional<DailyChallenge> update(Long id, DailyChallenge patch) {
        DailyChallenge existing = dailyChallengeRepository.findById(id).orElse(null);
        if (existing == null) return Optional.empty();

        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getReward() != 0) existing.setReward(patch.getReward());

        return Optional.of(dailyChallengeRepository.save(existing));
    }

    public void assignDailyChallengesToUser(User user) {
        List<DailyChallenge> todayChallenges = findRandom(3);
        LocalDate today = LocalDate.now();

        for (DailyChallenge challenge : todayChallenges) {
            DailyChallengeUserId entryId = new DailyChallengeUserId();
            entryId.setDailyChallengeId(challenge.getId());
            entryId.setUserId(user.getId());
            if (!dailyChallengeUserRepository.existsById(entryId)) {
                DailyChallengeUser entry = new DailyChallengeUser();
                entry.setId(entryId);
                entry.setDailyChallenge(challenge);
                entry.setUser(user);
                entry.setCompleted(false);
                entry.setDate(today);
                dailyChallengeUserRepository.save(entry);
            }
        }
    }

    public List<DailyChallengeUser> findByUser(Long userId) {
        return dailyChallengeUserRepository.findByUserId(userId);
    }

    @Transactional
    public Optional<DailyChallengeUser> completeChallenge(Long challengeId, Long userId) {
        DailyChallengeUserId entryId = new DailyChallengeUserId();
        entryId.setDailyChallengeId(challengeId);
        entryId.setUserId(userId);
        DailyChallengeUser entry = dailyChallengeUserRepository.findById(entryId).orElse(null);
        if (entry == null) return Optional.empty();

        entry.setCompleted(true);
        return Optional.of(dailyChallengeUserRepository.save(entry));
    }

    @Transactional
    public void deleteById(Long id) {
        dailyChallengeUserRepository.deleteByDailyChallengeId(id);
        dailyChallengeRepository.deleteById(id);
    }
}
