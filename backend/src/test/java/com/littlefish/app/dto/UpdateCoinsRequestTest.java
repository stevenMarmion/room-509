package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UpdateCoinsRequest")
class UpdateCoinsRequestTest {

    @Test @DisplayName("coins field is accessible")
    void accessor() {
        UpdateCoinsRequest req = new UpdateCoinsRequest(500);
        assertThat(req.coins()).isEqualTo(500);
    }

    @Test @DisplayName("zero coins is a valid value")
    void zeroCoins() {
        UpdateCoinsRequest req = new UpdateCoinsRequest(0);
        assertThat(req.coins()).isZero();
    }

    @Test @DisplayName("two instances with same value are equal")
    void equalsAndHashCode() {
        UpdateCoinsRequest a = new UpdateCoinsRequest(100);
        UpdateCoinsRequest b = new UpdateCoinsRequest(100);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test @DisplayName("toString contains the coins value")
    void toStringContainsValue() {
        UpdateCoinsRequest req = new UpdateCoinsRequest(250);
        assertThat(req.toString()).contains("250");
    }
}
