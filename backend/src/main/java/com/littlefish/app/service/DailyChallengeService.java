package com.littlefish.app.service;

import com.littlefish.app.model.DailyChallenge;
import com.littlefish.app.repository.DailyChallengeRepository;
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
}
