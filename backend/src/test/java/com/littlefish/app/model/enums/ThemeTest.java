package com.littlefish.app.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Theme")
class ThemeTest {

    @Test @DisplayName("has exactly two values: LIGHT and DARK")
    void values() {
        assertThat(Theme.values()).containsExactlyInAnyOrder(Theme.LIGHT, Theme.DARK);
    }

    @Test @DisplayName("valueOf LIGHT returns LIGHT")
    void valueOfLight() {
        assertThat(Theme.valueOf("LIGHT")).isEqualTo(Theme.LIGHT);
    }

    @Test @DisplayName("valueOf DARK returns DARK")
    void valueOfDark() {
        assertThat(Theme.valueOf("DARK")).isEqualTo(Theme.DARK);
    }

    @Test @DisplayName("invalid name throws IllegalArgumentException")
    void invalidName() {
        assertThatThrownBy(() -> Theme.valueOf("SYSTEM"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
