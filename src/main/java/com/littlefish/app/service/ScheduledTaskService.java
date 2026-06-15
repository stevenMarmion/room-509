package com.littlefish.app.service;

import com.littlefish.app.model.Fish;
import com.littlefish.app.model.User;
import com.littlefish.app.repository.FishRepository;
import com.littlefish.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final FishRepository fishRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void decreaseFishLifePoints() {
        List<Fish> allFish = fishRepository.findAll();
        for (Fish fish : allFish) {
            if (fish.getLifePoints() > 0) {
                fish.setLifePoints(fish.getLifePoints() - 1);
            }
        }
        fishRepository.saveAll(allFish);
    }

    @Scheduled(fixedRate = 60000)
    public void increaseUserCoins() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.setCoins(user.getCoins() + 5);
        }
        userRepository.saveAll(allUsers);
    }
}
