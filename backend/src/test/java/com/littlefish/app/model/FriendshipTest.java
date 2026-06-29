package com.littlefish.app.model;

import com.littlefish.app.model.enums.FriendStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Friendship")
class FriendshipTest {

    private Friendship friendship;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = new User(); alice.setId(1L); alice.setPseudo("alice");
        bob   = new User(); bob.setId(2L);   bob.setPseudo("bob");

        friendship = new Friendship();
        friendship.setId(1L);
        friendship.setRequester(alice);
        friendship.setAddressee(bob);
        friendship.setStatus(FriendStatus.PENDING);
        friendship.setSince(LocalDateTime.of(2026, 6, 1, 12, 0));
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("getters / setters")
    class GettersSetters {

        @Test @DisplayName("getId returns the set id")
        void getId() {
            assertThat(friendship.getId()).isEqualTo(1L);
        }

        @Test @DisplayName("getRequester returns alice")
        void getRequester() {
            assertThat(friendship.getRequester().getPseudo()).isEqualTo("alice");
        }

        @Test @DisplayName("getAddressee returns bob")
        void getAddressee() {
            assertThat(friendship.getAddressee().getPseudo()).isEqualTo("bob");
        }

        @Test @DisplayName("getStatus returns PENDING")
        void getStatus() {
            assertThat(friendship.getStatus()).isEqualTo(FriendStatus.PENDING);
        }

        @Test @DisplayName("getSince returns the set timestamp")
        void getSince() {
            assertThat(friendship.getSince())
                .isEqualTo(LocalDateTime.of(2026, 6, 1, 12, 0));
        }

        @Test @DisplayName("setStatus to ACCEPTED is reflected by getStatus")
        void setStatusAccepted() {
            friendship.setStatus(FriendStatus.ACCEPTED);
            assertThat(friendship.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
        }

        @Test @DisplayName("setStatus to BLOCKED is reflected by getStatus")
        void setStatusBlocked() {
            friendship.setStatus(FriendStatus.BLOCKED);
            assertThat(friendship.getStatus()).isEqualTo(FriendStatus.BLOCKED);
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("status lifecycle")
    class StatusLifecycle {

        @Test @DisplayName("a new friendship starts as PENDING")
        void pendingOnCreation() {
            assertThat(friendship.getStatus()).isEqualTo(FriendStatus.PENDING);
        }

        @Test @DisplayName("status transitions correctly from PENDING to ACCEPTED")
        void pendingToAccepted() {
            friendship.setStatus(FriendStatus.ACCEPTED);
            assertThat(friendship.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
        }

        @Test @DisplayName("status transitions correctly from PENDING to BLOCKED")
        void pendingToBlocked() {
            friendship.setStatus(FriendStatus.BLOCKED);
            assertThat(friendship.getStatus()).isEqualTo(FriendStatus.BLOCKED);
        }

        @Test @DisplayName("requester and addressee are distinct users")
        void distinctUsers() {
            assertThat(friendship.getRequester())
                .isNotEqualTo(friendship.getAddressee());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("equals / hashCode")
    class EqualsHashCode {

        @Test @DisplayName("two friendships with same fields are equal")
        void equalWhenSameFields() {
            Friendship other = new Friendship();
            other.setId(1L);
            other.setRequester(alice);
            other.setAddressee(bob);
            other.setStatus(FriendStatus.PENDING);
            other.setSince(LocalDateTime.of(2026, 6, 1, 12, 0));

            assertThat(friendship).isEqualTo(other);
        }

        @Test @DisplayName("different id produces inequality")
        void notEqualDifferentId() {
            Friendship other = new Friendship();
            other.setId(99L);
            other.setStatus(FriendStatus.PENDING);

            assertThat(friendship).isNotEqualTo(other);
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("noArgsConstructor")
    class NoArgsConstructor {

        @Test @DisplayName("default constructor produces non-null object with null status")
        void defaultValues() {
            Friendship f = new Friendship();
            assertThat(f).isNotNull();
            assertThat(f.getId()).isNull();
            assertThat(f.getStatus()).isNull();
            assertThat(f.getRequester()).isNull();
            assertThat(f.getAddressee()).isNull();
        }
    }
}
