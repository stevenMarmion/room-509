package com.littlefish.app.repository;

import com.littlefish.app.model.Aquarium;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AquariumRepository")
class AquariumRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired AquariumRepository aquariumRepository;

    private Aquarium persistAquariumWithOwner(String pseudo, boolean isPublic, int level) {
        User u = new User();
        u.setPseudo(pseudo); u.setEmail(pseudo + "@test.com"); u.setPassword("hashed");
        u.setCoins(0); u.setRole(Role.USER); u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        u.setFriendships(new ArrayList<>()); u.setTrades(new ArrayList<>());
        u.setDailyChallengeEntries(new ArrayList<>());

        NotificationPreference pref = new NotificationPreference();
        pref.setUser(u); u.setNotificationPreference(pref);

        Aquarium aq = new Aquarium();
        aq.setName(pseudo + "'s Tank");
        aq.setPublic(isPublic);
        aq.setLevel(level);
        aq.setCapacity(5);
        aq.setUser(u);
        aq.setFish(new ArrayList<>());
        u.setAquarium(aq);

        em.persistAndFlush(u);
        return aq;
    }

    @Nested @DisplayName("save / findById")
    class SaveAndFind {

        @Test @DisplayName("save persists aquarium and assigns id")
        void savesAquarium() {
            Aquarium aq = persistAquariumWithOwner("alice", true, 2);
            assertThat(aq.getId()).isNotNull();
        }

        @Test @DisplayName("findById returns the persisted aquarium")
        void findById() {
            Aquarium aq = persistAquariumWithOwner("alice", true, 2);

            Optional<Aquarium> result = aquariumRepository.findById(aq.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("alice's Tank");
            assertThat(result.get().getLevel()).isEqualTo(2);
            assertThat(result.get().isPublic()).isTrue();
        }

        @Test @DisplayName("findById returns empty when id does not exist")
        void notFound() {
            assertThat(aquariumRepository.findById(999L)).isEmpty();
        }
    }

    @Nested @DisplayName("findAll")
    class FindAll {

        @Test @DisplayName("returns all persisted aquariums")
        void returnsAll() {
            persistAquariumWithOwner("alice", true, 1);
            persistAquariumWithOwner("bob",   false, 2);

            assertThat(aquariumRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested @DisplayName("save (update)")
    class Update {

        @Test @DisplayName("updating level and capacity is persisted")
        void updatesFields() {
            Aquarium aq = persistAquariumWithOwner("alice", false, 1);

            aq.setLevel(3);
            aq.setCapacity(15);
            aquariumRepository.save(aq);
            em.flush();
            em.clear();

            Aquarium updated = aquariumRepository.findById(aq.getId()).orElseThrow();
            assertThat(updated.getLevel()).isEqualTo(3);
            assertThat(updated.getCapacity()).isEqualTo(15);
        }

        @Test @DisplayName("toggling visibility is persisted")
        void togglesVisibility() {
            Aquarium aq = persistAquariumWithOwner("alice", false, 1);

            aq.setPublic(true);
            aquariumRepository.save(aq);
            em.flush();
            em.clear();

            assertThat(aquariumRepository.findById(aq.getId()).orElseThrow().isPublic()).isTrue();
        }
    }

    @Nested @DisplayName("deleteById")
    class DeleteById {

        @Test @DisplayName("removes aquarium from database")
        void deletes() {
            Aquarium aq = persistAquariumWithOwner("alice", false, 1);
            Long id = aq.getId();
            em.clear();

            aquariumRepository.deleteById(id);
            em.flush();

            assertThat(aquariumRepository.findById(id)).isEmpty();
        }
    }
}
