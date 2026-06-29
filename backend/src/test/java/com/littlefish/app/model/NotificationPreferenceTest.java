package com.littlefish.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificationPreference")
class NotificationPreferenceTest {

    private NotificationPreference pref;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(); user.setId(1L); user.setPseudo("alice");

        pref = new NotificationPreference();
        pref.setId(1L);
        pref.setUser(user);
        pref.setNotifyOnDeath(true);
        pref.setNotifyBeforeDeath(true);
        pref.setDailyReminder(false);
    }

    @Nested @DisplayName("getters / setters")
    class GettersSetters {

        @Test @DisplayName("getId returns the set id")
        void getId() {
            assertThat(pref.getId()).isEqualTo(1L);
        }

        @Test @DisplayName("isNotifyOnDeath returns true")
        void notifyOnDeath() {
            assertThat(pref.isNotifyOnDeath()).isTrue();
        }

        @Test @DisplayName("isNotifyBeforeDeath returns true")
        void notifyBeforeDeath() {
            assertThat(pref.isNotifyBeforeDeath()).isTrue();
        }

        @Test @DisplayName("isDailyReminder returns false by default in setUp")
        void dailyReminder() {
            assertThat(pref.isDailyReminder()).isFalse();
        }

        @Test @DisplayName("setNotifyOnDeath(false) is reflected")
        void setNotifyOnDeathFalse() {
            pref.setNotifyOnDeath(false);
            assertThat(pref.isNotifyOnDeath()).isFalse();
        }

        @Test @DisplayName("setDailyReminder(true) is reflected")
        void setDailyReminderTrue() {
            pref.setDailyReminder(true);
            assertThat(pref.isDailyReminder()).isTrue();
        }
    }

    @Nested @DisplayName("user association")
    class UserAssociation {

        @Test @DisplayName("getUser returns the linked user")
        void getUser() {
            assertThat(pref.getUser().getPseudo()).isEqualTo("alice");
        }

        @Test @DisplayName("user can be updated")
        void setUser() {
            User bob = new User(); bob.setId(2L); bob.setPseudo("bob");
            pref.setUser(bob);
            assertThat(pref.getUser().getPseudo()).isEqualTo("bob");
        }
    }

    @Nested @DisplayName("all flags combinations")
    class FlagsCombinations {

        @Test @DisplayName("all flags true")
        void allTrue() {
            pref.setNotifyOnDeath(true);
            pref.setNotifyBeforeDeath(true);
            pref.setDailyReminder(true);
            assertThat(pref.isNotifyOnDeath()).isTrue();
            assertThat(pref.isNotifyBeforeDeath()).isTrue();
            assertThat(pref.isDailyReminder()).isTrue();
        }

        @Test @DisplayName("all flags false")
        void allFalse() {
            pref.setNotifyOnDeath(false);
            pref.setNotifyBeforeDeath(false);
            pref.setDailyReminder(false);
            assertThat(pref.isNotifyOnDeath()).isFalse();
            assertThat(pref.isNotifyBeforeDeath()).isFalse();
            assertThat(pref.isDailyReminder()).isFalse();
        }
    }

    @Nested @DisplayName("noArgsConstructor")
    class NoArgsConstructor {

        @Test @DisplayName("default constructor produces non-null object with false flags")
        void defaultValues() {
            NotificationPreference p = new NotificationPreference();
            assertThat(p).isNotNull();
            assertThat(p.getId()).isNull();
            assertThat(p.isNotifyOnDeath()).isFalse();
            assertThat(p.isNotifyBeforeDeath()).isFalse();
            assertThat(p.isDailyReminder()).isFalse();
        }
    }
}
