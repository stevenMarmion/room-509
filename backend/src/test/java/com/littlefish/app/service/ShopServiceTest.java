package com.littlefish.app.service;

import com.littlefish.app.model.*;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShopService")
class ShopServiceTest {

    @Mock FoodRepository foodRepository;
    @Mock AquariumUpgradeRepository upgradeRepository;
    @Mock UserRepository userRepository;
    @Mock FishRepository fishRepository;
    @Mock AquariumRepository aquariumRepository;
    @Mock AquariumService aquariumService;

    @InjectMocks ShopService shopService;

    private User makeUser(String pseudo, int coins, int aquariumCapacity) {
        User u = new User();
        u.setPseudo(pseudo);
        u.setCoins(coins);
        u.setRole(Role.USER);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        Aquarium aq = new Aquarium();
        aq.setId(1L);
        aq.setCapacity(aquariumCapacity);
        aq.setLevel(1);
        aq.setFish(new ArrayList<>());
        u.setAquarium(aq);
        return u;
    }

    private Food makeFood(Long id, String name, int price, int nutrition) {
        Food f = new Food();
        f.setId(id);
        f.setName(name);
        f.setPrice(price);
        f.setNutritionValue(nutrition);
        return f;
    }

    private AquariumUpgrade makeUpgrade(Long id, String name, int price, int capBonus, int lvlBonus) {
        AquariumUpgrade u = new AquariumUpgrade();
        u.setId(id);
        u.setName(name);
        u.setPrice(price);
        u.setCapacityBonus(capBonus);
        u.setLevelBonus(lvlBonus);
        return u;
    }

    private Fish makeFishTemplate(Long id, String name, int price) {
        Fish f = new Fish();
        f.setId(id);
        f.setName(name);
        f.setSpecies("Clownfish");
        f.setColor("Orange");
        f.setSize(2);
        f.setAge(0);
        f.setLifePoints(100);
        f.setPrice(price);
        return f;
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("buyFood")
    class BuyFood {

        @Test @DisplayName("deducts coins and restores fish HP on success")
        void success() {
            Food food = makeFood(1L, "Premium Flakes", 25, 30);
            User user = makeUser("alice", 100, 5);

            // Add a fish with 70 HP to the aquarium
            Fish fish = new Fish();
            fish.setLifePoints(70);
            user.getAquarium().getFish().add(fish);

            when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = shopService.buyFood(1L, "alice");

            assertThat(result).isPresent();
            verify(userRepository).updateCoins("alice", 75);  // 100 - 25
            assertThat(fish.getLifePoints()).isEqualTo(100);   // 70 + 30 = 100 (capped)
        }

        @Test @DisplayName("caps fish HP at 100")
        void capsHpAt100() {
            Food food = makeFood(1L, "Super Feed", 10, 50);
            User user = makeUser("alice", 200, 5);

            Fish fish = new Fish();
            fish.setLifePoints(90);
            user.getAquarium().getFish().add(fish);

            when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            shopService.buyFood(1L, "alice");

            assertThat(fish.getLifePoints()).isEqualTo(100);
        }

        @Test @DisplayName("returns empty when user cannot afford food")
        void notEnoughCoins() {
            Food food = makeFood(1L, "Premium Flakes", 100, 30);
            User user = makeUser("alice", 50, 5);

            when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));

            assertThat(shopService.buyFood(1L, "alice")).isEmpty();
            verify(userRepository, never()).updateCoins(any(), anyInt());
        }

        @Test @DisplayName("returns empty when food not found")
        void foodNotFound() {
            when(foodRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(shopService.buyFood(99L, "alice")).isEmpty();
        }

        @Test @DisplayName("returns empty when user not found")
        void userNotFound() {
            Food food = makeFood(1L, "Pellets", 10, 10);
            when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
            when(userRepository.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(shopService.buyFood(1L, "ghost")).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("buyUpgrade")
    class BuyUpgrade {

        @Test @DisplayName("applies capacity bonus on success")
        void appliesCapacityBonus() {
            AquariumUpgrade upgrade = makeUpgrade(1L, "Small Extension", 100, 5, 0);
            User user = makeUser("alice", 200, 5);

            when(upgradeRepository.findById(1L)).thenReturn(Optional.of(upgrade));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));
            when(aquariumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = shopService.buyUpgrade(1L, "alice");

            assertThat(result).isPresent();
            verify(userRepository).updateCoins("alice", 100); // 200 - 100
            assertThat(user.getAquarium().getCapacity()).isEqualTo(10); // 5 + 5
        }

        @Test @DisplayName("applies level bonus and caps at 10")
        void appliesLevelBonusCappedAt10() {
            AquariumUpgrade upgrade = makeUpgrade(2L, "Level Up Kit", 200, 0, 1);
            User user = makeUser("alice", 500, 5);
            user.getAquarium().setLevel(9);

            when(upgradeRepository.findById(2L)).thenReturn(Optional.of(upgrade));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));
            when(aquariumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = shopService.buyUpgrade(2L, "alice");

            assertThat(result).isPresent();
            assertThat(user.getAquarium().getLevel()).isEqualTo(10);
        }

        @Test @DisplayName("refuses level upgrade when aquarium is already level 10")
        void refusesAtMaxLevel() {
            AquariumUpgrade upgrade = makeUpgrade(2L, "Level Up Kit", 200, 0, 1);
            User user = makeUser("alice", 500, 5);
            user.getAquarium().setLevel(10);

            when(upgradeRepository.findById(2L)).thenReturn(Optional.of(upgrade));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));

            assertThat(shopService.buyUpgrade(2L, "alice")).isEmpty();
            verify(aquariumRepository, never()).save(any());
        }

        @Test @DisplayName("returns empty when user cannot afford upgrade")
        void notEnoughCoins() {
            AquariumUpgrade upgrade = makeUpgrade(3L, "Deluxe Bundle", 400, 5, 1);
            User user = makeUser("alice", 100, 5);

            when(upgradeRepository.findById(3L)).thenReturn(Optional.of(upgrade));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));

            assertThat(shopService.buyUpgrade(3L, "alice")).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("buyFish")
    class BuyFish {

        @Test @DisplayName("creates a copy of the template fish for the buyer")
        void success() {
            Fish template = makeFishTemplate(1L, "Nemo", 50);
            User user = makeUser("alice", 200, 5);

            when(fishRepository.findById(1L)).thenReturn(Optional.of(template));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(aquariumService.addFishToAquarium(eq(1L), any())).thenAnswer(inv -> {
                Fish f = inv.getArgument(1);
                user.getAquarium().getFish().add(f);
                return Optional.of(user.getAquarium());
            });

            Optional<Aquarium> result = shopService.buyFish(1L, "alice");

            assertThat(result).isPresent();
            verify(userRepository).updateCoins("alice", 150); // 200 - 50
        }

        @Test @DisplayName("returns empty when aquarium is full")
        void aquariumFull() {
            Fish template = makeFishTemplate(1L, "Nemo", 50);
            User user = makeUser("alice", 200, 2);
            // Fill the aquarium to capacity
            user.getAquarium().setFish(new ArrayList<>(List.of(
                makeFishTemplate(10L, "A", 10),
                makeFishTemplate(11L, "B", 10)
            )));

            when(fishRepository.findById(1L)).thenReturn(Optional.of(template));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));

            assertThat(shopService.buyFish(1L, "alice")).isEmpty();
            verify(userRepository, never()).updateCoins(any(), anyInt());
        }

        @Test @DisplayName("returns empty when user cannot afford fish")
        void notEnoughCoins() {
            Fish template = makeFishTemplate(1L, "Sharky", 200);
            User user = makeUser("alice", 50, 5);

            when(fishRepository.findById(1L)).thenReturn(Optional.of(template));
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(user));

            assertThat(shopService.buyFish(1L, "alice")).isEmpty();
        }
    }
}
