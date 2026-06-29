package com.littlefish.app.model;

import com.littlefish.app.model.enums.TradeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Trade")
class TradeTest {

    private Trade trade;
    private User alice;
    private User bob;
    private Fish nemo;

    @BeforeEach
    void setUp() {
        alice = new User(); alice.setId(1L); alice.setPseudo("alice");
        bob   = new User(); bob.setId(2L);   bob.setPseudo("bob");

        nemo = new Fish();
        nemo.setId(10L);
        nemo.setName("Nemo");

        trade = new Trade();
        trade.setId(1L);
        trade.setInitiator(alice);
        trade.setReceiver(bob);
        trade.setPrice(80);
        trade.setStatus(TradeStatus.PENDING);
        trade.setCreatedAt(LocalDateTime.of(2026, 6, 25, 9, 0));
        trade.setFish(new ArrayList<>(List.of(nemo)));
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("getters / setters")
    class GettersSetters {

        @Test @DisplayName("getId returns the set id")
        void getId() {
            assertThat(trade.getId()).isEqualTo(1L);
        }

        @Test @DisplayName("getInitiator returns alice")
        void getInitiator() {
            assertThat(trade.getInitiator().getPseudo()).isEqualTo("alice");
        }

        @Test @DisplayName("getReceiver returns bob")
        void getReceiver() {
            assertThat(trade.getReceiver().getPseudo()).isEqualTo("bob");
        }

        @Test @DisplayName("getPrice returns the set price")
        void getPrice() {
            assertThat(trade.getPrice()).isEqualTo(80);
        }

        @Test @DisplayName("getStatus returns PENDING")
        void getStatus() {
            assertThat(trade.getStatus()).isEqualTo(TradeStatus.PENDING);
        }

        @Test @DisplayName("getCreatedAt returns the set timestamp")
        void getCreatedAt() {
            assertThat(trade.getCreatedAt())
                .isEqualTo(LocalDateTime.of(2026, 6, 25, 9, 0));
        }

        @Test @DisplayName("setStatus to ACCEPTED is reflected")
        void setStatusAccepted() {
            trade.setStatus(TradeStatus.ACCEPTED);
            assertThat(trade.getStatus()).isEqualTo(TradeStatus.ACCEPTED);
        }

        @Test @DisplayName("setStatus to REJECTED is reflected")
        void setStatusRejected() {
            trade.setStatus(TradeStatus.REJECTED);
            assertThat(trade.getStatus()).isEqualTo(TradeStatus.REJECTED);
        }

        @Test @DisplayName("setPrice mutates the price field")
        void setPrice() {
            trade.setPrice(150);
            assertThat(trade.getPrice()).isEqualTo(150);
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("fish list")
    class FishList {

        @Test @DisplayName("fish list is populated in setUp")
        void fishListPopulated() {
            assertThat(trade.getFish()).hasSize(1);
            assertThat(trade.getFish().get(0).getName()).isEqualTo("Nemo");
        }

        @Test @DisplayName("multiple fish can be attached to a trade")
        void multipleFish() {
            Fish dory = new Fish(); dory.setId(11L); dory.setName("Dory");
            trade.getFish().add(dory);

            assertThat(trade.getFish()).hasSize(2);
        }

        @Test @DisplayName("fish can be cleared from the trade")
        void clearFish() {
            trade.setFish(new ArrayList<>());
            assertThat(trade.getFish()).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("status lifecycle")
    class StatusLifecycle {

        @Test @DisplayName("PENDING → ACCEPTED transition")
        void pendingToAccepted() {
            assertThat(trade.getStatus()).isEqualTo(TradeStatus.PENDING);
            trade.setStatus(TradeStatus.ACCEPTED);
            assertThat(trade.getStatus()).isEqualTo(TradeStatus.ACCEPTED);
        }

        @Test @DisplayName("PENDING → REJECTED transition")
        void pendingToRejected() {
            trade.setStatus(TradeStatus.REJECTED);
            assertThat(trade.getStatus()).isEqualTo(TradeStatus.REJECTED);
        }

        @Test @DisplayName("initiator and receiver are distinct")
        void distinctParties() {
            assertThat(trade.getInitiator()).isNotEqualTo(trade.getReceiver());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("equals / hashCode")
    class EqualsHashCode {

        @Test @DisplayName("two trades with same id and fields are equal")
        void equalWhenSameFields() {
            Trade other = new Trade();
            other.setId(1L);
            other.setInitiator(alice);
            other.setReceiver(bob);
            other.setPrice(80);
            other.setStatus(TradeStatus.PENDING);
            other.setCreatedAt(LocalDateTime.of(2026, 6, 25, 9, 0));
            other.setFish(new ArrayList<>(List.of(nemo)));

            assertThat(trade).isEqualTo(other);
        }

        @Test @DisplayName("different id produces inequality")
        void notEqualDifferentId() {
            Trade other = new Trade();
            other.setId(99L);

            assertThat(trade).isNotEqualTo(other);
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("noArgsConstructor")
    class NoArgsConstructor {

        @Test @DisplayName("default constructor produces non-null object with null status")
        void defaultValues() {
            Trade t = new Trade();
            assertThat(t).isNotNull();
            assertThat(t.getId()).isNull();
            assertThat(t.getStatus()).isNull();
            assertThat(t.getPrice()).isZero();
        }
    }
}
