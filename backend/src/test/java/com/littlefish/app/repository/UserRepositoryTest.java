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
@DisplayName("UserRepository")
class UserRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired UserRepository userRepository;

    private User persistUser(String pseudo, String email, Role role) {
        User u = new User();
        u.setPseudo(pseudo);
        u.setEmail(email);
        u.setPassword("hashed");
        u.setCoins(100);
        u.setRole(role);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        u.setFriendships(new ArrayList<>());
        u.setTrades(new ArrayList<>());
        u.setDailyChallengeEntries(new ArrayList<>());

        NotificationPreference pref = new NotificationPreference();
        pref.setNotifyOnDeath(true);
        pref.setNotifyBeforeDeath(true);
        pref.setDailyReminder(false);
        pref.setUser(u);
        u.setNotificationPreference(pref);

        Aquarium aq = new Aquarium();
        aq.setName(pseudo + "'s Tank");
        aq.setPublic(false);
        aq.setLevel(1);
        aq.setCapacity(5);
        aq.setUser(u);
        aq.setFish(new ArrayList<>());
        u.setAquarium(aq);

        return em.persistAndFlush(u);
    }

    @Nested @DisplayName("findByPseudo")
    class FindByPseudo {

        @Test @DisplayName("returns user when pseudo exists")
        void found() {
            persistUser("alice", "alice@test.com", Role.USER);

            Optional<User> result = userRepository.findByPseudo("alice");

            assertThat(result).isPresent();
            assertThat(result.get().getPseudo()).isEqualTo("alice");
            assertThat(result.get().getEmail()).isEqualTo("alice@test.com");
        }

        @Test @DisplayName("returns empty when pseudo does not exist")
        void notFound() {
            assertThat(userRepository.findByPseudo("ghost")).isEmpty();
        }

        @Test @DisplayName("is case-sensitive — ALICE != alice")
        void caseSensitive() {
            persistUser("alice", "alice@test.com", Role.USER);

            assertThat(userRepository.findByPseudo("ALICE")).isEmpty();
        }
    }

    @Nested @DisplayName("findByEmail")
    class FindByEmail {

        @Test @DisplayName("returns user when email exists")
        void found() {
            persistUser("alice", "alice@test.com", Role.USER);

            Optional<User> result = userRepository.findByEmail("alice@test.com");

            assertThat(result).isPresent();
            assertThat(result.get().getPseudo()).isEqualTo("alice");
        }

        @Test @DisplayName("returns empty when email does not exist")
        void notFound() {
            assertThat(userRepository.findByEmail("nobody@test.com")).isEmpty();
        }
    }

    @Nested @DisplayName("deleteByPseudo")
    class DeleteByPseudo {

        @Test @DisplayName("removes user from database")
        void deletesUser() {
            persistUser("alice", "alice@test.com", Role.USER);

            userRepository.deleteByPseudo("alice");
            em.flush();

            assertThat(userRepository.findByPseudo("alice")).isEmpty();
        }

        @Test @DisplayName("no-op when pseudo does not exist")
        void noOpWhenNotFound() {
            long countBefore = userRepository.count();

            userRepository.deleteByPseudo("ghost");
            em.flush();

            assertThat(userRepository.count()).isEqualTo(countBefore);
        }
    }

    @Nested @DisplayName("updateCoins")
    class UpdateCoins {

        @Test @DisplayName("sets coins to the given amount")
        void setsCoins() {
            persistUser("alice", "alice@test.com", Role.USER);

            userRepository.updateCoins("alice", 999);
            em.flush();
            em.clear();  // evict cache so we re-read from DB

            User updated = userRepository.findByPseudo("alice").orElseThrow();
            assertThat(updated.getCoins()).isEqualTo(999);
        }

        @Test @DisplayName("sets coins to 0 (edge case)")
        void setsToZero() {
            persistUser("alice", "alice@test.com", Role.USER);

            userRepository.updateCoins("alice", 0);
            em.flush();
            em.clear();

            assertThat(userRepository.findByPseudo("alice").orElseThrow().getCoins()).isZero();
        }
    }

    @Nested @DisplayName("findAll / save / deleteById")
    class CrudBasics {

        @Test @DisplayName("findAll returns all persisted users")
        void findAll() {
            persistUser("alice", "alice@test.com", Role.USER);
            persistUser("bob",   "bob@test.com",   Role.USER);

            assertThat(userRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test @DisplayName("save persists a new user and returns it with an id")
        void save() {
            User u = new User();
            u.setPseudo("newbie");
            u.setEmail("newbie@test.com");
            u.setPassword("hashed");
            u.setCoins(0);
            u.setRole(Role.USER);
            u.setTheme(Theme.LIGHT);
            u.setCreatedAt(LocalDateTime.now());
            u.setFriendships(new ArrayList<>());
            u.setTrades(new ArrayList<>());
            u.setDailyChallengeEntries(new ArrayList<>());

            Aquarium aq = new Aquarium();
            aq.setName("newbie's Tank"); aq.setLevel(1); aq.setCapacity(5);
            aq.setPublic(false); aq.setUser(u); aq.setFish(new ArrayList<>());
            u.setAquarium(aq);

            NotificationPreference pref = new NotificationPreference();
            pref.setUser(u); u.setNotificationPreference(pref);

            User saved = userRepository.save(u);

            assertThat(saved.getId()).isNotNull();
            assertThat(userRepository.findByPseudo("newbie")).isPresent();
        }

        @Test @DisplayName("deleteById removes user")
        void deleteById() {
            User u = persistUser("temp", "temp@test.com", Role.USER);
            Long id = u.getId();

            userRepository.deleteById(id);
            em.flush();

            assertThat(userRepository.findById(id)).isEmpty();
        }
    }
}
