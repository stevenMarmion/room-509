package com.littlefish.app.repository;

import com.littlefish.app.model.*;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("DailyChallengeUserRepository")
class DailyChallengeUserRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired DailyChallengeUserRepository dailyChallengeUserRepository;

    private User alice;
    private User bob;
    private DailyChallenge challengeFeed;
    private DailyChallenge challengeVisit;

    @BeforeEach
    void setUp() {
        alice = persistUser("alice", "alice@test.com");
        bob   = persistUser("bob",   "bob@test.com");

        challengeFeed = persistChallenge("Morning Feed", 20);
        challengeVisit = persistChallenge("Visit Friend", 15);
    }

    private User persistUser(String pseudo, String email) {
        User u = new User();
        u.setPseudo(pseudo); u.setEmail(email); u.setPassword("hashed");
        u.setCoins(100); u.setRole(Role.USER); u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        u.setFriendships(new ArrayList<>()); u.setTrades(new ArrayList<>());
        u.setDailyChallengeEntries(new ArrayList<>());

        NotificationPreference pref = new NotificationPreference();
        pref.setUser(u); u.setNotificationPreference(pref);

        Aquarium aq = new Aquarium();
        aq.setName(pseudo + "'s Tank"); aq.setLevel(1); aq.setCapacity(5);
        aq.setPublic(false); aq.setUser(u); aq.setFish(new ArrayList<>());
        u.setAquarium(aq);

        return em.persistAndFlush(u);
    }

    private DailyChallenge persistChallenge(String name, int reward) {
        DailyChallenge c = new DailyChallenge();
        c.setName(name); c.setReward(reward);
        c.setDescription("Do " + name);
        c.setUserEntries(new ArrayList<>());
        return em.persistAndFlush(c);
    }

    private DailyChallengeUser persistEntry(DailyChallenge challenge, User user, boolean completed) {
        DailyChallengeUserId id = new DailyChallengeUserId();
        id.setDailyChallengeId(challenge.getId());
        id.setUserId(user.getId());

        DailyChallengeUser entry = new DailyChallengeUser();
        entry.setId(id);
        entry.setDailyChallenge(challenge);
        entry.setUser(user);
        entry.setCompleted(completed);
        entry.setDate(LocalDate.now());
        return em.persistAndFlush(entry);
    }

    @Nested @DisplayName("findByUserId")
    class FindByUserId {

        @Test @DisplayName("returns all challenges assigned to alice")
        void returnsAliceChallenges() {
            persistEntry(challengeFeed,  alice, false);
            persistEntry(challengeVisit, alice, true);
            persistEntry(challengeFeed,  bob,   false);

            List<DailyChallengeUser> result = dailyChallengeUserRepository.findByUserId(alice.getId());

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getUser().getId().equals(alice.getId()));
        }

        @Test @DisplayName("returns empty when user has no challenges")
        void emptyForUnassignedUser() {
            List<DailyChallengeUser> result = dailyChallengeUserRepository.findByUserId(alice.getId());

            assertThat(result).isEmpty();
        }

        @Test @DisplayName("returns mix of completed and non-completed entries")
        void mixedCompletionStatus() {
            persistEntry(challengeFeed,  alice, true);
            persistEntry(challengeVisit, alice, false);

            List<DailyChallengeUser> result = dailyChallengeUserRepository.findByUserId(alice.getId());

            assertThat(result).hasSize(2);
            assertThat(result.stream().filter(DailyChallengeUser::isCompleted).count()).isEqualTo(1);
            assertThat(result.stream().filter(e -> !e.isCompleted()).count()).isEqualTo(1);
        }
    }

    @Nested @DisplayName("deleteByDailyChallengeId")
    class DeleteByChallengeId {

        @Test @DisplayName("removes all entries for the given challenge")
        void deletesAllEntriesForChallenge() {
            persistEntry(challengeFeed,  alice, false);
            persistEntry(challengeFeed,  bob,   true);
            persistEntry(challengeVisit, alice, false);

            dailyChallengeUserRepository.deleteByDailyChallengeId(challengeFeed.getId());
            em.flush();
            em.clear();

            // challengeFeed entries gone
            assertThat(dailyChallengeUserRepository
                .findByUserId(alice.getId()).stream()
                .filter(e -> e.getId().getDailyChallengeId().equals(challengeFeed.getId()))
                .count()).isZero();

            // challengeVisit entry intact
            assertThat(dailyChallengeUserRepository.findByUserId(alice.getId())).hasSize(1);
        }

        @Test @DisplayName("no-op when challenge has no user entries")
        void noOpWhenNoEntries() {
            DailyChallenge empty = persistChallenge("Empty", 0);

            // should not throw
            dailyChallengeUserRepository.deleteByDailyChallengeId(empty.getId());
            em.flush();
        }
    }

    @Nested @DisplayName("existsById (composite key)")
    class ExistsById {

        @Test @DisplayName("returns true when entry exists")
        void exists() {
            persistEntry(challengeFeed, alice, false);

            DailyChallengeUserId id = new DailyChallengeUserId();
            id.setDailyChallengeId(challengeFeed.getId());
            id.setUserId(alice.getId());

            assertThat(dailyChallengeUserRepository.existsById(id)).isTrue();
        }

        @Test @DisplayName("returns false when entry does not exist")
        void notExists() {
            DailyChallengeUserId id = new DailyChallengeUserId();
            id.setDailyChallengeId(challengeFeed.getId());
            id.setUserId(alice.getId());

            assertThat(dailyChallengeUserRepository.existsById(id)).isFalse();
        }
    }
}
