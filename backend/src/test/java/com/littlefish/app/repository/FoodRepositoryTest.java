package com.littlefish.app.repository;

import com.littlefish.app.model.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("FoodRepository")
class FoodRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired FoodRepository foodRepository;

    private Food persistFood(String name, int price, int nutrition) {
        Food f = new Food();
        f.setName(name);
        f.setPrice(price);
        f.setNutritionValue(nutrition);
        return em.persistAndFlush(f);
    }

    @Nested @DisplayName("save / findById")
    class SaveAndFind {

        @Test @DisplayName("persists food and assigns id")
        void saves() {
            Food f = persistFood("Premium Flakes", 25, 30);
            assertThat(f.getId()).isNotNull();
        }

        @Test @DisplayName("findById returns food with all fields")
        void findById() {
            Food f = persistFood("Premium Flakes", 25, 30);

            Optional<Food> result = foodRepository.findById(f.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Premium Flakes");
            assertThat(result.get().getPrice()).isEqualTo(25);
            assertThat(result.get().getNutritionValue()).isEqualTo(30);
        }
    }

    @Nested @DisplayName("findAll")
    class FindAll {

        @Test @DisplayName("returns all persisted food items")
        void returnsAll() {
            persistFood("Basic Pellets", 10, 15);
            persistFood("Live Brine",    50, 55);

            assertThat(foodRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested @DisplayName("save (update)")
    class Update {

        @Test @DisplayName("updating price and nutritionValue is persisted")
        void updatesFields() {
            Food f = persistFood("Old Food", 10, 10);

            f.setPrice(20);
            f.setNutritionValue(25);
            foodRepository.save(f);
            em.flush();
            em.clear();

            Food updated = foodRepository.findById(f.getId()).orElseThrow();
            assertThat(updated.getPrice()).isEqualTo(20);
            assertThat(updated.getNutritionValue()).isEqualTo(25);
        }
    }

    @Nested @DisplayName("deleteById")
    class DeleteById {

        @Test @DisplayName("removes food from database")
        void deletes() {
            Food f = persistFood("Temp Food", 5, 5);
            Long id = f.getId();

            foodRepository.deleteById(id);
            em.flush();

            assertThat(foodRepository.findById(id)).isEmpty();
        }
    }
}
