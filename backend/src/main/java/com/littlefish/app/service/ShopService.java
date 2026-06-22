package com.littlefish.app.service;

import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.AquariumUpgrade;
import com.littlefish.app.model.Fish;
import com.littlefish.app.model.Food;
import com.littlefish.app.model.User;
import com.littlefish.app.repository.AquariumRepository;
import com.littlefish.app.repository.AquariumUpgradeRepository;
import com.littlefish.app.repository.FishRepository;
import com.littlefish.app.repository.FoodRepository;
import com.littlefish.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final FoodRepository foodRepository;
    private final AquariumUpgradeRepository upgradeRepository;
    private final UserRepository userRepository;
    private final FishRepository fishRepository;
    private final AquariumRepository aquariumRepository;

    private final AquariumService aquariumService;

    public List<Food> findAllFood() {
        return foodRepository.findAll();
    }

    public Optional<Food> findFoodById(Long id) {
        return foodRepository.findById(id);
    }

    public List<AquariumUpgrade> findAllUpgrades() {
        return upgradeRepository.findAll();
    }

    public Optional<AquariumUpgrade> findUpgradeById(Long id) {
        return upgradeRepository.findById(id);
    }

    public List<Fish> findAllFish() {
        return fishRepository.findAll();
    }

    @Transactional
    public Optional<User> buyFood(Long foodId, String pseudo) {
        Food food = foodRepository.findById(foodId).orElse(null);
        User user = userRepository.findByPseudo(pseudo).orElse(null);

        if (food == null || user == null) {
            return Optional.empty();
        }
        if (user.getCoins() < food.getPrice()) {
            return Optional.empty();
        }

        userRepository.updateCoins(pseudo, user.getCoins() - food.getPrice());

        // Applique la nutrition à chaque poisson de l'aquarium
        List<Fish> fishList = user.getAquarium().getFish();
        for (Fish fish : fishList) {
            int newHp = Math.min(100, fish.getLifePoints() + food.getNutritionValue());
            fish.setLifePoints(newHp);
            fish.setLastFedAt(LocalDateTime.now());
            fishRepository.save(fish);
        }

        return Optional.of(userRepository.save(user));
    }

    // ── Achat upgrade aquarium ────────────────────────────────────
    // Applique directement capacity_bonus et level_bonus sur aquarium
    @Transactional
    public Optional<User> buyUpgrade(Long upgradeId, String pseudo) {
        AquariumUpgrade upgrade = upgradeRepository.findById(upgradeId).orElse(null);
        User user = userRepository.findByPseudo(pseudo).orElse(null);

        if (upgrade == null || user == null) {
            return Optional.empty();
        }
        if (user.getCoins() < upgrade.getPrice()) {
            return Optional.empty();
        }

        Aquarium aq = user.getAquarium();
        if (upgrade.getLevelBonus() > 0 && aq.getLevel() >= 10) {
            return Optional.empty();
        }

        userRepository.updateCoins(pseudo, user.getCoins() - upgrade.getPrice());
        aq.setCapacity(aq.getCapacity() + upgrade.getCapacityBonus());
        aq.setLevel(Math.min(10, aq.getLevel() + upgrade.getLevelBonus()));

        aquariumRepository.save(aq);
        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public Optional<Aquarium> buyFish(Long fishId, String pseudo) {
        Fish fishTemplate = fishRepository.findById(fishId).orElse(null);
        User user = userRepository.findByPseudo(pseudo).orElse(null);

        if (fishTemplate == null || user == null)          return Optional.empty();
        if (user.getCoins() < fishTemplate.getPrice())     return Optional.empty();

        Aquarium aquarium = user.getAquarium();
        if (aquarium.getFish().size() >= aquarium.getCapacity()) return Optional.empty();

        // Créer un nouveau fish (copie du template, pas déplacer l'existant)
        Fish newFish = new Fish();
        newFish.setName(fishTemplate.getName());
        newFish.setSpecies(fishTemplate.getSpecies());
        newFish.setColor(fishTemplate.getColor());
        newFish.setSize(fishTemplate.getSize());
        newFish.setAge(0);
        newFish.setLifePoints(100);
        newFish.setPrice(fishTemplate.getPrice());
        newFish.setLastFedAt(LocalDateTime.now());

        userRepository.updateCoins(pseudo, user.getCoins() - fishTemplate.getPrice());
        userRepository.save(user);

        return aquariumService.addFishToAquarium(aquarium.getId(), newFish);
    }
}