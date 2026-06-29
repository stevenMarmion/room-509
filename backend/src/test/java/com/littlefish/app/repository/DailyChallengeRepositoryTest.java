package com.littlefish.app.repository;

import com.littlefish.app.model.DailyChallenge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("DailyChallengeRepository")
class DailyChallengeRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired DailyChallengeRepository dailyChallengeRepository;

    private DailyChallenge persistChallenge(String name, int reward, String description) {
        DailyChallenge c = new DailyChallenge();
        c.setName(name);
        c.setReward(reward);
        c.setDescription(description);
        c.setUserEntries(new ArrayList<>());
        return em.persistAndFlush(c);
    }

    @Nested @DisplayName("save / findById")
    class SaveAndFind {

        @Test @DisplayName("persists challenge and assigns id")
        void saves() {
            DailyChallenge c = persistChallenge("Morning Feed", 20, "Feed all fish");
            assertThat(c.getId()).isNotNull();
        }

        @Test @DisplayName("findById returns all fields")
        void findById() {
            DailyChallenge c = persistChallenge("Morning Feed", 20, "Feed all fish");

            Optional<DailyChallenge> result = dailyChallengeRepository.findById(c.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Morning Feed");
            assertThat(result.get().getReward()).isEqualTo(20);
            assertThat(result.get().getDescription()).isEqualTo("Feed all fish");
        }
    }

    @Nested @DisplayName("findAll")
    class FindAll {

        @Test @DisplayName("returns all persisted challenges")
        void returnsAll() {
            persistChallenge("A", 10, "Do A");
            persistChallenge("B", 20, "Do B");
            persistChallenge("C", 30, "Do C");

            assertThat(dailyChallengeRepository.findAll()).hasSizeGreaterThanOrEqualTo(3);
        }
    }

    @Nested @DisplayName("save (update)")
    class Update {

        @Test @DisplayName("updating reward and name is persisted")
        void updatesFields() {
            DailyChallenge c = persistChallenge("Old Name", 10, "desc");

            c.setName("New Name");
            c.setReward(50);
            dailyChallengeRepository.save(c);
            em.flush();
            em.clear();

            DailyChallenge updated = dailyChallengeRepository.findById(c.getId()).orElseThrow();
            assertThat(updated.getName()).isEqualTo("New Name");
            assertThat(updated.getReward()).isEqualTo(50);
        }
    }

    @Nested @DisplayName("deleteById")
    class DeleteById {

        @Test @DisplayName("removes challenge from database")
        void deletes() {
            DailyChallenge c = persistChallenge("Temp", 5, "temp");
            Long id = c.getId();

            dailyChallengeRepository.deleteById(id);
            em.flush();

            assertThat(dailyChallengeRepository.findById(id)).isEmpty();
        }
    }
}
