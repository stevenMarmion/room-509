package com.littlefish.app.service;

import com.littlefish.app.model.Fish;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.repository.FishRepository;
import com.littlefish.app.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduledTaskService")
class ScheduledTaskServiceTest {

    @Mock FishRepository fishRepository;
    @Mock UserRepository userRepository;

    @InjectMocks ScheduledTaskService scheduledTaskService;

    private Fish makeFish(Long id, int lifePoints) {
        Fish f = new Fish();
        f.setId(id);
        f.setLifePoints(lifePoints);
        return f;
    }

    private User makeUser(Long id, String pseudo, int coins) {
        User u = new User();
        u.setId(id);
        u.setPseudo(pseudo);
        u.setCoins(coins);
        u.setRole(Role.USER);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        return u;
    }

    @Nested @DisplayName("decreaseFishLifePoints")
    class DecreaseFishLifePoints {

        @Test @DisplayName("decreases life points by 1 for all fish above 0")
        void decreasesNormalFish() {
            Fish f1 = makeFish(1L, 100);
            Fish f2 = makeFish(2L, 50);
            when(fishRepository.findAll()).thenReturn(List.of(f1, f2));

            scheduledTaskService.decreaseFishLifePoints();

            assertThat(f1.getLifePoints()).isEqualTo(99);
            assertThat(f2.getLifePoints()).isEqualTo(49);
            verify(fishRepository).saveAll(List.of(f1, f2));
        }

        @Test @DisplayName("does not go below 0 for dead fish")
        void doesNotGoBelowZero() {
            Fish f = makeFish(1L, 0);
            when(fishRepository.findAll()).thenReturn(List.of(f));

            scheduledTaskService.decreaseFishLifePoints();

            assertThat(f.getLifePoints()).isEqualTo(0);
        }

        @Test @DisplayName("handles empty fish list without error")
        void emptyList() {
            when(fishRepository.findAll()).thenReturn(List.of());

            scheduledTaskService.decreaseFishLifePoints();

            verify(fishRepository).saveAll(List.of());
        }

        @Test @DisplayName("brings fish from 1 HP to 0 HP (not negative)")
        void oneHpGoesToZero() {
            Fish f = makeFish(1L, 1);
            when(fishRepository.findAll()).thenReturn(List.of(f));

            scheduledTaskService.decreaseFishLifePoints();

            assertThat(f.getLifePoints()).isEqualTo(0);
        }
    }

    @Nested @DisplayName("increaseUserCoins")
    class IncreaseUserCoins {

        @Test @DisplayName("adds 5 coins to every user")
        void addsCoinsToAll() {
            User alice = makeUser(1L, "alice", 100);
            User bob   = makeUser(2L, "bob",   0);
            when(userRepository.findAll()).thenReturn(List.of(alice, bob));

            scheduledTaskService.increaseUserCoins();

            assertThat(alice.getCoins()).isEqualTo(105);
            assertThat(bob.getCoins()).isEqualTo(5);
            verify(userRepository).saveAll(List.of(alice, bob));
        }

        @Test @DisplayName("handles empty user list without error")
        void emptyList() {
            when(userRepository.findAll()).thenReturn(List.of());

            scheduledTaskService.increaseUserCoins();

            verify(userRepository).saveAll(List.of());
        }

        @Test @DisplayName("saves updated users to repository")
        @SuppressWarnings("unchecked")
        void savesUpdatedUsers() {
            User alice = makeUser(1L, "alice", 50);
            when(userRepository.findAll()).thenReturn(List.of(alice));

            scheduledTaskService.increaseUserCoins();

            ArgumentCaptor<List<User>> captor = ArgumentCaptor.forClass(List.class);
            verify(userRepository).saveAll(captor.capture());
            assertThat(captor.getValue().get(0).getCoins()).isEqualTo(55);
        }
    }
}
