package com.littlefish.app.service;

import com.littlefish.app.model.DailyChallenge;
import com.littlefish.app.repository.DailyChallengeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyChallengeService {

    private final DailyChallengeRepository dailyChallengeRepository;

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
        if (patch.getDate() != null) existing.setDate(patch.getDate());
        if (patch.isCompleted() != existing.isCompleted()) existing.setCompleted(patch.isCompleted());

        return Optional.of(dailyChallengeRepository.save(existing));
    }

    @Transactional
    public void deleteById(Long id) {
        dailyChallengeRepository.deleteById(id);
    }
}
