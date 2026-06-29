package com.littlefish.app.service;

import com.littlefish.app.model.DailyChallenge;
import com.littlefish.app.model.DailyChallengeUser;
import com.littlefish.app.model.DailyChallengeUserId;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.repository.DailyChallengeRepository;
import com.littlefish.app.repository.DailyChallengeUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DailyChallengeService")
class DailyChallengeServiceTest {

    @Mock DailyChallengeRepository dailyChallengeRepository;
    @Mock DailyChallengeUserRepository dailyChallengeUserRepository;

    @InjectMocks DailyChallengeService dailyChallengeService;

    private DailyChallenge makeChallenge(Long id, String name, int reward) {
        DailyChallenge c = new DailyChallenge();
        c.setId(id);
        c.setName(name);
        c.setReward(reward);
        c.setDescription("Do " + name);
        c.setUserEntries(new ArrayList<>());
        return c;
    }

    private User makeUser(Long id, String pseudo) {
        User u = new User();
        u.setId(id);
        u.setPseudo(pseudo);
        u.setRole(Role.USER);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        return u;
    }

    @Nested @DisplayName("findAll")
    class FindAll {
        @Test @DisplayName("returns all challenges from repository")
        void returnsList() {
            List<DailyChallenge> challenges = List.of(
                makeChallenge(1L, "Morning Feed", 20),
                makeChallenge(2L, "Visit Friend", 15)
            );
            when(dailyChallengeRepository.findAll()).thenReturn(challenges);

            assertThat(dailyChallengeService.findAll()).hasSize(2);
        }
    }

    @Nested @DisplayName("findRandom")
    class FindRandom {

        @Test @DisplayName("returns exactly count elements when pool is large enough")
        void returnsCorrectCount() {
            List<DailyChallenge> all = new ArrayList<>(List.of(
                makeChallenge(1L, "A", 10),
                makeChallenge(2L, "B", 20),
                makeChallenge(3L, "C", 30),
                makeChallenge(4L, "D", 40)
            ));
            when(dailyChallengeRepository.findAll()).thenReturn(all);

            List<DailyChallenge> result = dailyChallengeService.findRandom(3);

            assertThat(result).hasSize(3);
        }

        @Test @DisplayName("returns all when pool is smaller than requested count")
        void returnsAllWhenSmallPool() {
            List<DailyChallenge> all = List.of(
                makeChallenge(1L, "A", 10),
                makeChallenge(2L, "B", 20)
            );
            when(dailyChallengeRepository.findAll()).thenReturn(all);

            List<DailyChallenge> result = dailyChallengeService.findRandom(5);

            assertThat(result).hasSize(2);
        }
    }

    @Nested @DisplayName("assignDailyChallengesToUser")
    class AssignChallenges {

        @Test @DisplayName("assigns up to 3 challenges and skips already assigned ones")
        void assignsNewChallenges() {
            User user = makeUser(1L, "alice");
            List<DailyChallenge> challenges = List.of(
                makeChallenge(1L, "Feed", 20),
                makeChallenge(2L, "Visit", 15),
                makeChallenge(3L, "Click", 25)
            );
            when(dailyChallengeRepository.findAll()).thenReturn(challenges);

            DailyChallengeUserId existingId = new DailyChallengeUserId();
            existingId.setDailyChallengeId(1L);
            existingId.setUserId(1L);
            when(dailyChallengeUserRepository.existsById(existingId)).thenReturn(true);
            when(dailyChallengeUserRepository.existsById(argThat(id ->
                !id.getDailyChallengeId().equals(1L)))).thenReturn(false);
            when(dailyChallengeUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            dailyChallengeService.assignDailyChallengesToUser(user);

            // Challenge 1 skipped (already assigned), challenges 2 and 3 saved
            verify(dailyChallengeUserRepository, times(2)).save(any());
        }
    }

    @Nested @DisplayName("completeChallenge")
    class CompleteChallenge {

        @Test @DisplayName("marks challenge as completed")
        void marksCompleted() {
            User user = makeUser(1L, "alice");
            DailyChallenge challenge = makeChallenge(1L, "Feed", 20);

            DailyChallengeUserId entryId = new DailyChallengeUserId();
            entryId.setDailyChallengeId(1L);
            entryId.setUserId(1L);

            DailyChallengeUser entry = new DailyChallengeUser();
            entry.setId(entryId);
            entry.setUser(user);
            entry.setDailyChallenge(challenge);
            entry.setCompleted(false);
            entry.setDate(LocalDate.now());

            when(dailyChallengeUserRepository.findById(any())).thenReturn(Optional.of(entry));
            when(dailyChallengeUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<DailyChallengeUser> result = dailyChallengeService.completeChallenge(1L, 1L);

            assertThat(result).isPresent();
            assertThat(result.get().isCompleted()).isTrue();
        }

        @Test @DisplayName("returns empty when entry not found")
        void entryNotFound() {
            when(dailyChallengeUserRepository.findById(any())).thenReturn(Optional.empty());

            assertThat(dailyChallengeService.completeChallenge(99L, 99L)).isEmpty();
        }
    }

    @Nested @DisplayName("update")
    class Update {

        @Test @DisplayName("updates name and reward when provided")
        void updatesFields() {
            DailyChallenge existing = makeChallenge(1L, "Old Name", 10);
            when(dailyChallengeRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(dailyChallengeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            DailyChallenge patch = new DailyChallenge();
            patch.setName("New Name");
            patch.setReward(50);

            Optional<DailyChallenge> result = dailyChallengeService.update(1L, patch);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("New Name");
            assertThat(result.get().getReward()).isEqualTo(50);
        }

        @Test @DisplayName("returns empty when challenge not found")
        void notFound() {
            when(dailyChallengeRepository.findById(99L)).thenReturn(Optional.empty());

            DailyChallenge patch = new DailyChallenge();
            patch.setName("X");

            assertThat(dailyChallengeService.update(99L, patch)).isEmpty();
        }
    }
}
