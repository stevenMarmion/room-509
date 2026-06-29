package com.littlefish.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DailyChallengeUser")
class DailyChallengeUserTest {

    private DailyChallengeUser entry;
    private DailyChallengeUserId entryId;
    private User user;
    private DailyChallenge challenge;

    @BeforeEach
    void setUp() {
        user = new User(); user.setId(1L); user.setPseudo("alice");

        challenge = new DailyChallenge();
        challenge.setId(2L);
        challenge.setName("Morning Feed");

        entryId = new DailyChallengeUserId();
        entryId.setUserId(1L);
        entryId.setDailyChallengeId(2L);

        entry = new DailyChallengeUser();
        entry.setId(entryId);
        entry.setUser(user);
        entry.setDailyChallenge(challenge);
        entry.setCompleted(false);
        entry.setDate(LocalDate.of(2026, 6, 25));
    }

    @Nested @DisplayName("getters / setters")
    class GettersSetters {

        @Test @DisplayName("getId returns the composite key")
        void getId() {
            assertThat(entry.getId()).isEqualTo(entryId);
        }

        @Test @DisplayName("getUser returns alice")
        void getUser() {
            assertThat(entry.getUser().getPseudo()).isEqualTo("alice");
        }

        @Test @DisplayName("getDailyChallenge returns Morning Feed")
        void getDailyChallenge() {
            assertThat(entry.getDailyChallenge().getName()).isEqualTo("Morning Feed");
        }

        @Test @DisplayName("isCompleted returns false by default")
        void isCompleted() {
            assertThat(entry.isCompleted()).isFalse();
        }

        @Test @DisplayName("setCompleted(true) is reflected")
        void setCompletedTrue() {
            entry.setCompleted(true);
            assertThat(entry.isCompleted()).isTrue();
        }

        @Test @DisplayName("getDate returns the set date")
        void getDate() {
            assertThat(entry.getDate()).isEqualTo(LocalDate.of(2026, 6, 25));
        }
    }

    @Nested @DisplayName("completion lifecycle")
    class CompletionLifecycle {

        @Test @DisplayName("entry starts as not completed")
        void startsNotCompleted() {
            assertThat(entry.isCompleted()).isFalse();
        }

        @Test @DisplayName("completing the entry sets completed to true")
        void completeEntry() {
            entry.setCompleted(true);
            assertThat(entry.isCompleted()).isTrue();
        }

        @Test @DisplayName("date is set to today's date")
        void dateIsToday() {
            entry.setDate(LocalDate.now());
            assertThat(entry.getDate()).isEqualTo(LocalDate.now());
        }
    }
}
