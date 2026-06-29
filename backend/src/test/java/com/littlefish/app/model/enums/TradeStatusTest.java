package com.littlefish.app.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TradeStatus")
class TradeStatusTest {

    @Test @DisplayName("has exactly three values: PENDING, ACCEPTED, REJECTED")
    void values() {
        assertThat(TradeStatus.values())
            .containsExactlyInAnyOrder(
                TradeStatus.PENDING,
                TradeStatus.ACCEPTED,
                TradeStatus.REJECTED
            );
    }

    @Test @DisplayName("valueOf REJECTED returns REJECTED")
    void valueOfRejected() {
        assertThat(TradeStatus.valueOf("REJECTED")).isEqualTo(TradeStatus.REJECTED);
    }

    @Test @DisplayName("invalid name throws IllegalArgumentException")
    void invalidName() {
        assertThatThrownBy(() -> TradeStatus.valueOf("CANCELLED"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("ordinal order: PENDING=0, ACCEPTED=1, REJECTED=2")
    void ordinalOrder() {
        assertThat(TradeStatus.PENDING.ordinal()).isZero();
        assertThat(TradeStatus.ACCEPTED.ordinal()).isEqualTo(1);
        assertThat(TradeStatus.REJECTED.ordinal()).isEqualTo(2);
    }
}
