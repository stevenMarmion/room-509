package com.littlefish.app.repository;

import com.littlefish.app.model.*;
import com.littlefish.app.model.enums.FriendStatus;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("FriendshipRepository")
class FriendshipRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired FriendshipRepository friendshipRepository;

    private User alice;
    private User bob;
    private User charlie;

    @BeforeEach
    void setUp() {
        alice   = persistUser("alice",   "alice@test.com");
        bob     = persistUser("bob",     "bob@test.com");
        charlie = persistUser("charlie", "charlie@test.com");
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

    private Friendship persistFriendship(User requester, User addressee, FriendStatus status) {
        Friendship f = new Friendship();
        f.setRequester(requester);
        f.setAddressee(addressee);
        f.setStatus(status);
        f.setSince(LocalDateTime.now());
        return em.persistAndFlush(f);
    }

    @Nested @DisplayName("findByAddressee_PseudoAndStatus")
    class FindByAddresseeAndStatus {

        @Test @DisplayName("returns pending requests received by bob")
        void returnsPendingForBob() {
            persistFriendship(alice, bob, FriendStatus.PENDING);
            persistFriendship(charlie, bob, FriendStatus.PENDING);
            persistFriendship(bob, alice, FriendStatus.ACCEPTED);

            List<Friendship> result = friendshipRepository
                .findByAddressee_PseudoAndStatus("bob", FriendStatus.PENDING);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(f -> f.getStatus() == FriendStatus.PENDING);
            assertThat(result).allMatch(f -> f.getAddressee().getPseudo().equals("bob"));
        }

        @Test @DisplayName("returns empty when no matching requests")
        void emptyWhenNoMatch() {
            persistFriendship(alice, bob, FriendStatus.ACCEPTED);

            List<Friendship> result = friendshipRepository
                .findByAddressee_PseudoAndStatus("bob", FriendStatus.PENDING);

            assertThat(result).isEmpty();
        }

        @Test @DisplayName("filters by status — ACCEPTED does not appear in PENDING results")
        void filtersStatus() {
            persistFriendship(alice, bob, FriendStatus.ACCEPTED);
            persistFriendship(charlie, bob, FriendStatus.PENDING);

            List<Friendship> pending = friendshipRepository
                .findByAddressee_PseudoAndStatus("bob", FriendStatus.PENDING);

            assertThat(pending).hasSize(1);
            assertThat(pending.get(0).getRequester().getPseudo()).isEqualTo("charlie");
        }
    }

    @Nested @DisplayName("findByRequesterAndAddressee")
    class FindByRequesterAndAddressee {

        @Test @DisplayName("returns friendship when pair exists")
        void found() {
            persistFriendship(alice, bob, FriendStatus.PENDING);

            Optional<Friendship> result = friendshipRepository
                .findByRequesterAndAddressee(alice, bob);

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(FriendStatus.PENDING);
        }

        @Test @DisplayName("returns empty for the reverse direction when not created")
        void reverseDirectionNotFound() {
            persistFriendship(alice, bob, FriendStatus.PENDING);

            Optional<Friendship> result = friendshipRepository
                .findByRequesterAndAddressee(bob, alice);

            assertThat(result).isEmpty();
        }

        @Test @DisplayName("returns empty when no relationship exists between the pair")
        void noRelationship() {
            Optional<Friendship> result = friendshipRepository
                .findByRequesterAndAddressee(alice, charlie);

            assertThat(result).isEmpty();
        }
    }

    @Nested @DisplayName("deleteByRequesterAndAddressee")
    class DeleteByRequesterAndAddressee {

        @Test @DisplayName("removes the friendship from the database")
        void deletesFriendship() {
            persistFriendship(alice, bob, FriendStatus.ACCEPTED);

            friendshipRepository.deleteByRequesterAndAddressee(alice, bob);
            em.flush();
            em.clear();

            assertThat(friendshipRepository.findByRequesterAndAddressee(alice, bob)).isEmpty();
        }

        @Test @DisplayName("does not remove the reverse direction")
        void onlyDeletesOneDirection() {
            persistFriendship(alice, bob, FriendStatus.ACCEPTED);
            persistFriendship(bob, alice, FriendStatus.ACCEPTED);

            friendshipRepository.deleteByRequesterAndAddressee(alice, bob);
            em.flush();
            em.clear();

            assertThat(friendshipRepository.findByRequesterAndAddressee(bob, alice)).isPresent();
        }
    }
}
