package com.littlefish.app.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Role")
class RoleTest {

    @Test @DisplayName("has exactly two values: USER and ADMIN")
    void values() {
        assertThat(Role.values()).containsExactlyInAnyOrder(Role.USER, Role.ADMIN);
    }

    @Test @DisplayName("valueOf USER returns USER")
    void valueOfUser() {
        assertThat(Role.valueOf("USER")).isEqualTo(Role.USER);
    }

    @Test @DisplayName("valueOf ADMIN returns ADMIN")
    void valueOfAdmin() {
        assertThat(Role.valueOf("ADMIN")).isEqualTo(Role.ADMIN);
    }

    @Test @DisplayName("invalid name throws IllegalArgumentException")
    void invalidName() {
        assertThatThrownBy(() -> Role.valueOf("SUPERADMIN"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("name() returns the enum constant name as a String")
    void name() {
        assertThat(Role.USER.name()).isEqualTo("USER");
        assertThat(Role.ADMIN.name()).isEqualTo("ADMIN");
    }
}
