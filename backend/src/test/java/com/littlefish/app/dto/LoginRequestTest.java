package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginRequest")
class LoginRequestTest {

    @Test @DisplayName("accessors return the values passed to the constructor")
    void accessors() {
        LoginRequest req = new LoginRequest("alice", "secret");
        assertThat(req.pseudo()).isEqualTo("alice");
        assertThat(req.password()).isEqualTo("secret");
    }

    @Test @DisplayName("two instances with same values are equal")
    void equalsAndHashCode() {
        LoginRequest a = new LoginRequest("alice", "secret");
        LoginRequest b = new LoginRequest("alice", "secret");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test @DisplayName("different password produces inequality")
    void notEqual() {
        LoginRequest a = new LoginRequest("alice", "secret");
        LoginRequest b = new LoginRequest("alice", "wrong");
        assertThat(a).isNotEqualTo(b);
    }

    @Test @DisplayName("toString contains pseudo and password")
    void toStringContainsFields() {
        LoginRequest req = new LoginRequest("alice", "secret");
        assertThat(req.toString()).contains("alice").contains("secret");
    }

    @Test @DisplayName("null pseudo is accepted")
    void nullPseudo() {
        LoginRequest req = new LoginRequest(null, "secret");
        assertThat(req.pseudo()).isNull();
    }
}
