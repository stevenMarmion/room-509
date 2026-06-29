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
@DisplayName("NotificationPreferenceRepository")
class NotificationPreferenceRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired NotificationPreferenceRepository notificationPreferenceRepository;

    private NotificationPreference persistPrefWithUser(
            String pseudo, boolean onDeath, boolean beforeDeath, boolean daily) {
        User u = new User();
        u.setPseudo(pseudo); u.setEmail(pseudo + "@test.com"); u.setPassword("hashed");
        u.setCoins(0); u.setRole(Role.USER); u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        u.setFriendships(new ArrayList<>()); u.setTrades(new ArrayList<>());
        u.setDailyChallengeEntries(new ArrayList<>());

        Aquarium aq = new Aquarium();
        aq.setName(pseudo + "'s Tank"); aq.setLevel(1); aq.setCapacity(5);
        aq.setPublic(false); aq.setUser(u); aq.setFish(new ArrayList<>());
        u.setAquarium(aq);

        NotificationPreference pref = new NotificationPreference();
        pref.setNotifyOnDeath(onDeath);
        pref.setNotifyBeforeDeath(beforeDeath);
        pref.setDailyReminder(daily);
        pref.setUser(u);
        u.setNotificationPreference(pref);

        em.persistAndFlush(u);
        return pref;
    }

    @Nested @DisplayName("save / findById")
    class SaveAndFind {

        @Test @DisplayName("persists preference with all boolean flags")
        void saves() {
            NotificationPreference pref = persistPrefWithUser("alice", true, false, true);

            Optional<NotificationPreference> result =
                notificationPreferenceRepository.findById(pref.getId());

            assertThat(result).isPresent();
            assertThat(result.get().isNotifyOnDeath()).isTrue();
            assertThat(result.get().isNotifyBeforeDeath()).isFalse();
            assertThat(result.get().isDailyReminder()).isTrue();
        }
    }

    @Nested @DisplayName("findAll")
    class FindAll {

        @Test @DisplayName("returns preferences for all users")
        void returnsAll() {
            persistPrefWithUser("alice", true,  true,  false);
            persistPrefWithUser("bob",   false, false, true);

            assertThat(notificationPreferenceRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested @DisplayName("save (update)")
    class Update {

        @Test @DisplayName("updating all flags is persisted")
        void updatesFlags() {
            NotificationPreference pref = persistPrefWithUser("alice", true, true, false);

            pref.setNotifyOnDeath(false);
            pref.setDailyReminder(true);
            notificationPreferenceRepository.save(pref);
            em.flush();
            em.clear();

            NotificationPreference updated =
                notificationPreferenceRepository.findById(pref.getId()).orElseThrow();
            assertThat(updated.isNotifyOnDeath()).isFalse();
            assertThat(updated.isDailyReminder()).isTrue();
        }
    }

    @Nested @DisplayName("deleteById")
    class DeleteById {

        @Test @DisplayName("removes preference from database")
        void deletes() {
            NotificationPreference pref = persistPrefWithUser("alice", true, true, true);
            Long id = pref.getId();
            em.clear();

            notificationPreferenceRepository.deleteById(id);
            em.flush();

            assertThat(notificationPreferenceRepository.findById(id)).isEmpty();
        }
    }
}
