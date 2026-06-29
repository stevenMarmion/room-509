package com.littlefish.app.repository;

import com.littlefish.app.model.AquariumUpgrade;
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
@DisplayName("AquariumUpgradeRepository")
class AquariumUpgradeRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired AquariumUpgradeRepository upgradeRepository;

    private AquariumUpgrade persistUpgrade(String name, int price, int capBonus, int lvlBonus) {
        AquariumUpgrade u = new AquariumUpgrade();
        u.setName(name);
        u.setPrice(price);
        u.setCapacityBonus(capBonus);
        u.setLevelBonus(lvlBonus);
        return em.persistAndFlush(u);
    }

    @Nested @DisplayName("save / findById")
    class SaveAndFind {

        @Test @DisplayName("persists upgrade and assigns id")
        void saves() {
            AquariumUpgrade u = persistUpgrade("Small Extension", 100, 5, 0);
            assertThat(u.getId()).isNotNull();
        }

        @Test @DisplayName("findById returns all fields")
        void findById() {
            AquariumUpgrade u = persistUpgrade("Deluxe Bundle", 400, 5, 1);

            Optional<AquariumUpgrade> result = upgradeRepository.findById(u.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Deluxe Bundle");
            assertThat(result.get().getCapacityBonus()).isEqualTo(5);
            assertThat(result.get().getLevelBonus()).isEqualTo(1);
        }
    }

    @Nested @DisplayName("findAll")
    class FindAll {

        @Test @DisplayName("returns all persisted upgrades")
        void returnsAll() {
            persistUpgrade("A", 100, 5, 0);
            persistUpgrade("B", 200, 0, 1);

            assertThat(upgradeRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested @DisplayName("save (update)")
    class Update {

        @Test @DisplayName("updating bonuses is persisted")
        void updatesBonuses() {
            AquariumUpgrade u = persistUpgrade("Basic", 50, 2, 0);

            u.setCapacityBonus(10);
            u.setLevelBonus(1);
            upgradeRepository.save(u);
            em.flush();
            em.clear();

            AquariumUpgrade updated = upgradeRepository.findById(u.getId()).orElseThrow();
            assertThat(updated.getCapacityBonus()).isEqualTo(10);
            assertThat(updated.getLevelBonus()).isEqualTo(1);
        }
    }

    @Nested @DisplayName("deleteById")
    class DeleteById {

        @Test @DisplayName("removes upgrade from database")
        void deletes() {
            AquariumUpgrade u = persistUpgrade("Temp", 10, 1, 0);
            Long id = u.getId();

            upgradeRepository.deleteById(id);
            em.flush();

            assertThat(upgradeRepository.findById(id)).isEmpty();
        }
    }
}
