package com.littlefish.app.service;

import com.littlefish.app.dto.UserUpdateDTO;
import com.littlefish.app.model.*;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.repository.FishRepository;
import com.littlefish.app.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock FishRepository fishRepository;
    @Mock DailyChallengeService dailyChallengeService;

    @InjectMocks UserService userService;

    private User makeUser(Long id, String pseudo, String email, int coins, Role role) {
        User u = new User();
        u.setId(id);
        u.setPseudo(pseudo);
        u.setEmail(email);
        u.setPassword("hashed");
        u.setCoins(coins);
        u.setRole(role);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        Aquarium aq = new Aquarium();
        aq.setFish(new ArrayList<>());
        aq.setCapacity(10);
        aq.setLevel(1);
        u.setAquarium(aq);
        u.setFriendships(new ArrayList<>());
        u.setTrades(new ArrayList<>());
        return u;
    }

    private Fish makeFishTemplate() {
        Fish f = new Fish();
        f.setId(1L);
        f.setName("Nemo");
        f.setSpecies("Clownfish");
        f.setColor("Orange");
        f.setSize(2);
        f.setAge(0);
        f.setLifePoints(100);
        return f;
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("findByPseudo")
    class FindByPseudo {
        @Test @DisplayName("returns user when pseudo exists")
        void found() {
            User u = makeUser(1L, "alice", "alice@test.com", 200, Role.USER);
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(u));

            Optional<User> result = userService.findByPseudo("alice");

            assertThat(result).isPresent().contains(u);
        }

        @Test @DisplayName("returns empty when pseudo does not exist")
        void notFound() {
            when(userRepository.findByPseudo("unknown")).thenReturn(Optional.empty());

            assertThat(userService.findByPseudo("unknown")).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("registerUser")
    class RegisterUser {

        @Test @DisplayName("saves user with 200 coins and USER role on success")
        void success() {
            User input = new User();
            input.setPseudo("newbie");
            input.setEmail("newbie@test.com");
            input.setPassword("hashed");

            when(fishRepository.findAll()).thenReturn(List.of(makeFishTemplate()));
            when(userRepository.findByPseudo("newbie")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("newbie@test.com")).thenReturn(Optional.empty());
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            User saved = userService.registerUser(input);

            assertThat(saved).isNotNull();
            assertThat(saved.getCoins()).isEqualTo(200);
            assertThat(saved.getRole()).isEqualTo(Role.USER);
            assertThat(saved.getTheme()).isEqualTo(Theme.LIGHT);
            assertThat(saved.getAquarium()).isNotNull();
            assertThat(saved.getNotificationPreference()).isNotNull();
            verify(dailyChallengeService).assignDailyChallengesToUser(saved);
        }

        @Test @DisplayName("returns null when pseudo already taken")
        void pseudoAlreadyTaken() {
            User existing = makeUser(1L, "alice", "alice@test.com", 100, Role.USER);
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(existing));

            User input = new User();
            input.setPseudo("alice");
            input.setEmail("other@test.com");

            assertThat(userService.registerUser(input)).isNull();
            verify(userRepository, never()).save(any());
        }

        @Test @DisplayName("returns null when email already taken")
        void emailAlreadyTaken() {
            User existing = makeUser(1L, "alice", "alice@test.com", 100, Role.USER);
            when(userRepository.findByPseudo("newpseudo")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(existing));

            User input = new User();
            input.setPseudo("newpseudo");
            input.setEmail("alice@test.com");

            assertThat(userService.registerUser(input)).isNull();
            verify(userRepository, never()).save(any());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("update")
    class Update {

        @Test @DisplayName("updates pseudo and email when provided")
        void updatesFields() {
            User u = makeUser(1L, "alice", "alice@test.com", 200, Role.USER);
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(u));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UserUpdateDTO dto = new UserUpdateDTO("alice2", "alice2@test.com", null, null);
            Optional<User> result = userService.update("alice", dto);

            assertThat(result).isPresent();
            assertThat(result.get().getPseudo()).isEqualTo("alice2");
            assertThat(result.get().getEmail()).isEqualTo("alice2@test.com");
        }

        @Test @DisplayName("updates theme when provided")
        void updatesTheme() {
            User u = makeUser(1L, "alice", "alice@test.com", 200, Role.USER);
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(u));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UserUpdateDTO dto = new UserUpdateDTO(null, null, null, Theme.DARK);
            Optional<User> result = userService.update("alice", dto);

            assertThat(result).isPresent();
            assertThat(result.get().getTheme()).isEqualTo(Theme.DARK);
        }

        @Test @DisplayName("returns empty when user not found")
        void userNotFound() {
            when(userRepository.findByPseudo("ghost")).thenReturn(Optional.empty());

            UserUpdateDTO dto = new UserUpdateDTO("ghost2", null, null, null);
            assertThat(userService.update("ghost", dto)).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("earnCoins")
    class EarnCoins {

        @Test @DisplayName("adds coins to user balance")
        void addsCoins() {
            User u = makeUser(1L, "alice", "alice@test.com", 100, Role.USER);
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(u));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = userService.earnCoins("alice", 50);

            assertThat(result).isPresent();
            assertThat(result.get().getCoins()).isEqualTo(150);
        }

        @Test @DisplayName("returns empty when user not found")
        void userNotFound() {
            when(userRepository.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(userService.earnCoins("ghost", 50)).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("updateRole")
    class UpdateRole {

        @Test @DisplayName("promotes user to ADMIN")
        void promotesToAdmin() {
            User u = makeUser(1L, "alice", "alice@test.com", 100, Role.USER);
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(u));
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = userService.updateRole("alice", Role.ADMIN);

            assertThat(result).isPresent();
            assertThat(result.get().getRole()).isEqualTo(Role.ADMIN);
        }

        @Test @DisplayName("returns empty when user not found")
        void userNotFound() {
            when(userRepository.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(userService.updateRole("ghost", Role.ADMIN)).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("updateCoinsAdmin")
    class UpdateCoinsAdmin {

        @Test @DisplayName("sets coins to the given amount")
        void setsCoins() {
            User u = makeUser(1L, "alice", "alice@test.com", 100, Role.USER);
            when(userRepository.findByPseudo("alice")).thenReturn(Optional.of(u));

            Optional<User> result = userService.updateCoinsAdmin("alice", 999);

            assertThat(result).isPresent();
            verify(userRepository).updateCoins("alice", 999);
        }

        @Test @DisplayName("returns empty when user not found")
        void userNotFound() {
            when(userRepository.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(userService.updateCoinsAdmin("ghost", 100)).isEmpty();
        }
    }
}
