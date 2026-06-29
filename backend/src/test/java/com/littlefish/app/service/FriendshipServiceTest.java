package com.littlefish.app.service;

import com.littlefish.app.dto.FriendshipDTO;
import com.littlefish.app.model.Friendship;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.FriendStatus;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.repository.FriendshipRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FriendshipService")
class FriendshipServiceTest {

    @Mock FriendshipRepository friendshipRepository;
    @Mock UserService userService;

    @InjectMocks FriendshipService friendshipService;

    private User makeUser(Long id, String pseudo) {
        User u = new User();
        u.setId(id);
        u.setPseudo(pseudo);
        u.setRole(Role.USER);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        u.setFriendships(new ArrayList<>());
        return u;
    }

    private Friendship makeFriendship(User requester, User addressee, FriendStatus status) {
        Friendship f = new Friendship();
        f.setRequester(requester);
        f.setAddressee(addressee);
        f.setStatus(status);
        f.setSince(LocalDateTime.now());
        return f;
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("addFriend")
    class AddFriend {

        @Test @DisplayName("creates a PENDING friendship request")
        void createsPendingRequest() {
            User alice = makeUser(1L, "alice");
            User bob   = makeUser(2L, "bob");

            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(userService.findByPseudo("bob")).thenReturn(Optional.of(bob));
            when(friendshipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = friendshipService.addFriend("alice", "bob");

            assertThat(result).isPresent();
            assertThat(alice.getFriendships()).hasSize(1);
            assertThat(alice.getFriendships().get(0).getStatus()).isEqualTo(FriendStatus.PENDING);
            assertThat(alice.getFriendships().get(0).getAddressee()).isEqualTo(bob);
        }

        @Test @DisplayName("returns empty when sender not found")
        void senderNotFound() {
            when(userService.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(friendshipService.addFriend("ghost", "bob")).isEmpty();
            verify(friendshipRepository, never()).save(any());
        }

        @Test @DisplayName("returns empty when recipient not found")
        void recipientNotFound() {
            User alice = makeUser(1L, "alice");
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(userService.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(friendshipService.addFriend("alice", "ghost")).isEmpty();
            verify(friendshipRepository, never()).save(any());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("acceptFriend")
    class AcceptFriend {

        @Test @DisplayName("sets both directions to ACCEPTED and creates symmetric link")
        void acceptsBothDirections() {
            User alice = makeUser(1L, "alice");
            User bob   = makeUser(2L, "bob");
            Friendship pending = makeFriendship(alice, bob, FriendStatus.PENDING);

            when(userService.findByPseudo("bob")).thenReturn(Optional.of(bob));
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            // bob is accepting alice's request
            when(friendshipRepository.findByRequesterAndAddressee(alice, bob))
                .thenReturn(Optional.of(pending));
            when(friendshipRepository.findByRequesterAndAddressee(bob, alice))
                .thenReturn(Optional.empty());
            when(friendshipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = friendshipService.acceptFriend("bob", "alice");

            assertThat(result).isPresent();
            assertThat(pending.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
            // symmetric save also called
            verify(friendshipRepository, times(2)).save(any());
        }

        @Test @DisplayName("returns empty when original friendship not found")
        void originalNotFound() {
            User alice = makeUser(1L, "alice");
            User bob   = makeUser(2L, "bob");

            when(userService.findByPseudo("bob")).thenReturn(Optional.of(bob));
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(friendshipRepository.findByRequesterAndAddressee(alice, bob))
                .thenReturn(Optional.empty());

            assertThat(friendshipService.acceptFriend("bob", "alice")).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("rejectFriend")
    class RejectFriend {

        @Test @DisplayName("sets friendship status to BLOCKED")
        void blocksRequest() {
            User alice = makeUser(1L, "alice");
            User bob   = makeUser(2L, "bob");
            Friendship pending = makeFriendship(alice, bob, FriendStatus.PENDING);

            when(userService.findByPseudo("bob")).thenReturn(Optional.of(bob));
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(friendshipRepository.findByRequesterAndAddressee(alice, bob))
                .thenReturn(Optional.of(pending));
            when(friendshipRepository.findByRequesterAndAddressee(bob, alice))
                .thenReturn(Optional.empty());
            when(friendshipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<User> result = friendshipService.rejectFriend("bob", "alice");

            assertThat(result).isPresent();
            assertThat(pending.getStatus()).isEqualTo(FriendStatus.BLOCKED);
        }

        @Test @DisplayName("returns empty when users not found")
        void usersNotFound() {
            when(userService.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(friendshipService.rejectFriend("ghost", "alice")).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("findAcceptedFriends")
    class FindAcceptedFriends {

        @Test @DisplayName("returns only ACCEPTED friendships")
        void returnsOnlyAccepted() {
            User alice = makeUser(1L, "alice");
            User bob   = makeUser(2L, "bob");
            User charlie = makeUser(3L, "charlie");

            Friendship accepted = makeFriendship(alice, bob, FriendStatus.ACCEPTED);
            Friendship pending  = makeFriendship(alice, charlie, FriendStatus.PENDING);
            alice.setFriendships(new ArrayList<>(List.of(accepted, pending)));

            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));

            List<FriendshipDTO> result = friendshipService.findAcceptedFriends("alice");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).pseudo()).isEqualTo("bob");
        }

        @Test @DisplayName("returns empty list when user not found")
        void userNotFound() {
            when(userService.findByPseudo("ghost")).thenReturn(Optional.empty());

            assertThat(friendshipService.findAcceptedFriends("ghost")).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("toDTO")
    class ToDTO {

        @Test @DisplayName("maps requester-side friendship to correct friend pseudo")
        void mapsRequesterSide() {
            User alice = makeUser(1L, "alice");
            User bob   = makeUser(2L, "bob");
            Friendship f = makeFriendship(alice, bob, FriendStatus.ACCEPTED);

            FriendshipDTO dto = friendshipService.toDTO(f, "alice");

            // From alice's perspective, the friend is bob
            assertThat(dto.pseudo()).isEqualTo("bob");
            assertThat(dto.status()).isEqualTo(FriendStatus.ACCEPTED);
        }

        @Test @DisplayName("maps addressee-side friendship to correct friend pseudo")
        void mapsAddresseeSide() {
            User alice = makeUser(1L, "alice");
            User bob   = makeUser(2L, "bob");
            Friendship f = makeFriendship(alice, bob, FriendStatus.ACCEPTED);

            FriendshipDTO dto = friendshipService.toDTO(f, "bob");

            // From bob's perspective, the friend is alice
            assertThat(dto.pseudo()).isEqualTo("alice");
        }
    }
}
