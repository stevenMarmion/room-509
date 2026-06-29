package com.littlefish.app.repository;

import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Fish;
import com.littlefish.app.model.NotificationPreference;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("FishRepository")
class FishRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired FishRepository fishRepository;

    private Aquarium persistAquariumWithOwner(String pseudo) {
        User u = new User();
        u.setPseudo(pseudo);
        u.setEmail(pseudo + "@test.com");
        u.setPassword("hashed");
        u.setCoins(100);
        u.setRole(Role.USER);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        u.setFriendships(new ArrayList<>());
        u.setTrades(new ArrayList<>());
        u.setDailyChallengeEntries(new ArrayList<>());

        NotificationPreference pref = new NotificationPreference();
        pref.setUser(u); u.setNotificationPreference(pref);

        Aquarium aq = new Aquarium();
        aq.setName(pseudo + "'s Tank");
        aq.setLevel(1); aq.setCapacity(5);
        aq.setPublic(false);
        aq.setUser(u);
        aq.setFish(new ArrayList<>());
        u.setAquarium(aq);

        em.persistAndFlush(u);
        return aq;
    }

    private Fish persistFish(String name, int lifePoints, Aquarium aq) {
        Fish f = new Fish();
        f.setName(name);
        f.setSpecies("Clownfish");
        f.setColor("Orange");
        f.setSize(2);
        f.setAge(1);
        f.setLifePoints(lifePoints);
        f.setPrice(50);
        f.setLastFedAt(LocalDateTime.now());
        f.setAquarium(aq);
        f.setTrades(new ArrayList<>());
        return em.persistAndFlush(f);
    }

    @Nested @DisplayName("save")
    class Save {

        @Test @DisplayName("persists fish and assigns an id")
        void persistsFish() {
            Aquarium aq = persistAquariumWithOwner("alice");
            Fish f = persistFish("Nemo", 100, aq);

            assertThat(f.getId()).isNotNull();
            assertThat(fishRepository.findById(f.getId())).isPresent();
        }
    }

    @Nested @DisplayName("findById")
    class FindById {

        @Test @DisplayName("returns fish when id exists")
        void found() {
            Aquarium aq = persistAquariumWithOwner("alice");
            Fish f = persistFish("Nemo", 100, aq);

            Optional<Fish> result = fishRepository.findById(f.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Nemo");
            assertThat(result.get().getLifePoints()).isEqualTo(100);
        }

        @Test @DisplayName("returns empty when id does not exist")
        void notFound() {
            assertThat(fishRepository.findById(999L)).isEmpty();
        }
    }

    @Nested @DisplayName("findAll")
    class FindAll {

        @Test @DisplayName("returns all persisted fish")
        void returnsAll() {
            Aquarium aq = persistAquariumWithOwner("alice");
            persistFish("Nemo", 100, aq);
            persistFish("Dory", 80, aq);

            List<Fish> all = fishRepository.findAll();

            assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested @DisplayName("deleteById")
    class DeleteById {

        @Test @DisplayName("removes fish from database")
        void deletesFish() {
            Aquarium aq = persistAquariumWithOwner("alice");
            Fish f = persistFish("Nemo", 100, aq);
            Long id = f.getId();

            fishRepository.deleteById(id);
            em.flush();

            assertThat(fishRepository.findById(id)).isEmpty();
        }
    }

    @Nested @DisplayName("saveAll")
    class SaveAll {

        @Test @DisplayName("persists multiple fish at once")
        void persistsAll() {
            Aquarium aq = persistAquariumWithOwner("alice");

            Fish f1 = new Fish(); f1.setName("A"); f1.setLifePoints(90); f1.setPrice(10); f1.setSize(1); f1.setAge(0);
            f1.setAquarium(aq); f1.setTrades(new ArrayList<>());
            Fish f2 = new Fish(); f2.setName("B"); f2.setLifePoints(50); f2.setPrice(10); f2.setSize(1); f2.setAge(0);
            f2.setAquarium(aq); f2.setTrades(new ArrayList<>());

            fishRepository.saveAll(List.of(f1, f2));
            em.flush();

            assertThat(fishRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test @DisplayName("updating lifePoints via saveAll is persisted")
        void updatesLifePoints() {
            Aquarium aq = persistAquariumWithOwner("alice");
            Fish f = persistFish("Nemo", 100, aq);

            f.setLifePoints(75);
            fishRepository.saveAll(List.of(f));
            em.flush();
            em.clear();

            assertThat(fishRepository.findById(f.getId()).orElseThrow().getLifePoints()).isEqualTo(75);
        }
    }
}
