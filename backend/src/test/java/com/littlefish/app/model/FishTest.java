package com.littlefish.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Fish")
class FishTest {

    private Fish fish;

    @BeforeEach
    void setUp() {
        fish = new Fish();
        fish.setId(1L);
        fish.setName("Nemo");
        fish.setSpecies("Clownfish");
        fish.setColor("Orange & White");
        fish.setPrice(50);
        fish.setSize(2);
        fish.setAge(1);
        fish.setLifePoints(100);
        fish.setLastFedAt(LocalDateTime.of(2026, 6, 25, 8, 0));
        fish.setTrades(new ArrayList<>());
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("getters / setters")
    class GettersSetters {

        @Test @DisplayName("getId returns the set id")
        void getId() {
            assertThat(fish.getId()).isEqualTo(1L);
        }

        @Test @DisplayName("getName returns the set name")
        void getName() {
            assertThat(fish.getName()).isEqualTo("Nemo");
        }

        @Test @DisplayName("getSpecies returns the set species")
        void getSpecies() {
            assertThat(fish.getSpecies()).isEqualTo("Clownfish");
        }

        @Test @DisplayName("getColor returns the set color")
        void getColor() {
            assertThat(fish.getColor()).isEqualTo("Orange & White");
        }

        @Test @DisplayName("getPrice returns the set price")
        void getPrice() {
            assertThat(fish.getPrice()).isEqualTo(50);
        }

        @Test @DisplayName("getSize returns the set size")
        void getSize() {
            assertThat(fish.getSize()).isEqualTo(2);
        }

        @Test @DisplayName("getAge returns the set age")
        void getAge() {
            assertThat(fish.getAge()).isEqualTo(1);
        }

        @Test @DisplayName("getLifePoints returns the set life points")
        void getLifePoints() {
            assertThat(fish.getLifePoints()).isEqualTo(100);
        }

        @Test @DisplayName("getLastFedAt returns the set timestamp")
        void getLastFedAt() {
            assertThat(fish.getLastFedAt())
                .isEqualTo(LocalDateTime.of(2026, 6, 25, 8, 0));
        }

        @Test @DisplayName("setLifePoints to 0 represents a dead fish")
        void setLifePointsToZero() {
            fish.setLifePoints(0);
            assertThat(fish.getLifePoints()).isZero();
        }

        @Test @DisplayName("setLastFedAt updates the timestamp")
        void setLastFedAt() {
            LocalDateTime now = LocalDateTime.now();
            fish.setLastFedAt(now);
            assertThat(fish.getLastFedAt()).isEqualTo(now);
        }

        @Test @DisplayName("setName updates the name")
        void setName() {
            fish.setName("Dory");
            assertThat(fish.getName()).isEqualTo("Dory");
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("aquarium association")
    class AquariumAssociation {

        @Test @DisplayName("aquarium can be set and retrieved")
        void setAndGetAquarium() {
            Aquarium aq = new Aquarium();
            aq.setId(5L);
            aq.setName("Test Tank");
            fish.setAquarium(aq);

            assertThat(fish.getAquarium()).isNotNull();
            assertThat(fish.getAquarium().getId()).isEqualTo(5L);
        }

        @Test @DisplayName("aquarium is null by default")
        void aquariumNullByDefault() {
            Fish f = new Fish();
            assertThat(f.getAquarium()).isNull();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("life points semantics")
    class LifePointsSemantics {

        @Test @DisplayName("fish is alive when lifePoints > 0")
        void aliveWhenPositive() {
            fish.setLifePoints(50);
            assertThat(fish.getLifePoints()).isPositive();
        }

        @Test @DisplayName("fish is dead when lifePoints == 0")
        void deadWhenZero() {
            fish.setLifePoints(0);
            assertThat(fish.getLifePoints()).isZero();
        }

        @Test @DisplayName("lifePoints can be decremented one by one to zero")
        void decrementToZero() {
            fish.setLifePoints(3);
            fish.setLifePoints(fish.getLifePoints() - 1);
            fish.setLifePoints(fish.getLifePoints() - 1);
            fish.setLifePoints(fish.getLifePoints() - 1);
            assertThat(fish.getLifePoints()).isZero();
        }

        @Test @DisplayName("lifePoints capped at 100 when adding nutrition (simulated)")
        void capAt100() {
            fish.setLifePoints(90);
            int nutrition = 30;
            int newHp = Math.min(100, fish.getLifePoints() + nutrition);
            fish.setLifePoints(newHp);
            assertThat(fish.getLifePoints()).isEqualTo(100);
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("equals / hashCode  (Lombok @Data default)")
    class EqualsHashCode {

        @Test @DisplayName("two fish with identical fields are equal")
        void equalWhenSameFields() {
            Fish other = new Fish();
            other.setId(1L);
            other.setName("Nemo");
            other.setSpecies("Clownfish");
            other.setColor("Orange & White");
            other.setPrice(50);
            other.setSize(2);
            other.setAge(1);
            other.setLifePoints(100);
            other.setLastFedAt(LocalDateTime.of(2026, 6, 25, 8, 0));
            other.setTrades(new ArrayList<>());

            assertThat(fish).isEqualTo(other);
        }

        @Test @DisplayName("fish with different name are not equal")
        void notEqualDifferentName() {
            Fish other = new Fish();
            other.setId(2L);
            other.setName("Dory");

            assertThat(fish).isNotEqualTo(other);
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("noArgsConstructor")
    class NoArgsConstructor {

        @Test @DisplayName("default constructor produces non-null object with zero numeric fields")
        void defaultValues() {
            Fish f = new Fish();
            assertThat(f).isNotNull();
            assertThat(f.getId()).isNull();
            assertThat(f.getName()).isNull();
            assertThat(f.getLifePoints()).isZero();
            assertThat(f.getPrice()).isZero();
            assertThat(f.getSize()).isZero();
            assertThat(f.getAge()).isZero();
            assertThat(f.getLastFedAt()).isNull();
        }
    }
}
