package com.littlefish.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DailyChallenge")
class DailyChallengeTest {

    private DailyChallenge challenge;

    @BeforeEach
    void setUp() {
        challenge = new DailyChallenge();
        challenge.setId(1L);
        challenge.setName("Morning Feed");
        challenge.setReward(20);
        challenge.setDescription("Feed all fish in your aquarium");
        challenge.setUserEntries(new ArrayList<>());
    }

    @Nested @DisplayName("getters / setters")
    class GettersSetters {

        @Test @DisplayName("getId returns the set id")
        void getId() {
            assertThat(challenge.getId()).isEqualTo(1L);
        }

        @Test @DisplayName("getName returns the set name")
        void getName() {
            assertThat(challenge.getName()).isEqualTo("Morning Feed");
        }

        @Test @DisplayName("getReward returns the set reward")
        void getReward() {
            assertThat(challenge.getReward()).isEqualTo(20);
        }

        @Test @DisplayName("getDescription returns the set description")
        void getDescription() {
            assertThat(challenge.getDescription())
                .isEqualTo("Feed all fish in your aquarium");
        }

        @Test @DisplayName("setReward mutates correctly")
        void setReward() {
            challenge.setReward(50);
            assertThat(challenge.getReward()).isEqualTo(50);
        }

        @Test @DisplayName("setName mutates correctly")
        void setName() {
            challenge.setName("New Name");
            assertThat(challenge.getName()).isEqualTo("New Name");
        }
    }

    @Nested @DisplayName("userEntries list")
    class UserEntriesList {

        @Test @DisplayName("userEntries is initially empty")
        void initiallyEmpty() {
            assertThat(challenge.getUserEntries()).isEmpty();
        }

        @Test @DisplayName("userEntries can be populated")
        void addEntry() {
            DailyChallengeUser entry = new DailyChallengeUser();
            challenge.getUserEntries().add(entry);
            assertThat(challenge.getUserEntries()).hasSize(1);
        }
    }

    @Nested @DisplayName("noArgsConstructor")
    class NoArgsConstructor {

        @Test @DisplayName("default constructor produces non-null object")
        void defaultValues() {
            DailyChallenge c = new DailyChallenge();
            assertThat(c).isNotNull();
            assertThat(c.getId()).isNull();
            assertThat(c.getName()).isNull();
            assertThat(c.getReward()).isZero();
        }
    }
}