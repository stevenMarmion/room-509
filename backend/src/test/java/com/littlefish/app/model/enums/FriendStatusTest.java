package com.littlefish.app.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FriendStatus")
class FriendStatusTest {

    @Test @DisplayName("has exactly three values: PENDING, ACCEPTED, BLOCKED")
    void values() {
        assertThat(FriendStatus.values())
            .containsExactlyInAnyOrder(
                FriendStatus.PENDING,
                FriendStatus.ACCEPTED,
                FriendStatus.BLOCKED
            );
    }

    @Test @DisplayName("valueOf PENDING returns PENDING")
    void valueOfPending() {
        assertThat(FriendStatus.valueOf("PENDING")).isEqualTo(FriendStatus.PENDING);
    }

    @Test @DisplayName("valueOf ACCEPTED returns ACCEPTED")
    void valueOfAccepted() {
        assertThat(FriendStatus.valueOf("ACCEPTED")).isEqualTo(FriendStatus.ACCEPTED);
    }

    @Test @DisplayName("valueOf BLOCKED returns BLOCKED")
    void valueOfBlocked() {
        assertThat(FriendStatus.valueOf("BLOCKED")).isEqualTo(FriendStatus.BLOCKED);
    }

    @Test @DisplayName("invalid name throws IllegalArgumentException")
    void invalidName() {
        assertThatThrownBy(() -> FriendStatus.valueOf("DENIED"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ordinal order: PENDING=0, ACCEPTED=1, BLOCKED=2")
    void ordinalOrder() {
        assertThat(FriendStatus.PENDING.ordinal()).isZero();
        assertThat(FriendStatus.ACCEPTED.ordinal()).isEqualTo(1);
        assertThat(FriendStatus.BLOCKED.ordinal()).isEqualTo(2);
    }
}
