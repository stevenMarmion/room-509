package com.littlefish.app.service;

import com.littlefish.app.model.AquariumUpgrade;
import com.littlefish.app.model.Food;
import com.littlefish.app.repository.AquariumUpgradeRepository;
import com.littlefish.app.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final FoodRepository foodRepository;
    private final AquariumUpgradeRepository aquariumUpgradeRepository;

    public List<Food> findAllFood() {
        return foodRepository.findAll();
    }

    public Optional<Food> findFoodById(Long id) {
        return foodRepository.findById(id);
    }

    public Food saveFood(Food food) {
        return foodRepository.save(food);
    }

    public void deleteFoodById(Long id) {
        foodRepository.deleteById(id);
    }

    public List<AquariumUpgrade> findAllUpgrades() {
        return aquariumUpgradeRepository.findAll();
    }

    public Optional<AquariumUpgrade> findUpgradeById(Long id) {
        return aquariumUpgradeRepository.findById(id);
    }

    public AquariumUpgrade saveUpgrade(AquariumUpgrade upgrade) {
        return aquariumUpgradeRepository.save(upgrade);
    }

    public void deleteUpgradeById(Long id) {
        aquariumUpgradeRepository.deleteById(id);
    }
}
