package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegisterRequest")
class RegisterRequestTest {

    @Test @DisplayName("accessors return the values passed to the constructor")
    void accessors() {
        RegisterRequest req = new RegisterRequest("alice", "alice@test.com", "pass", "pass");
        assertThat(req.pseudo()).isEqualTo("alice");
        assertThat(req.email()).isEqualTo("alice@test.com");
        assertThat(req.password()).isEqualTo("pass");
        assertThat(req.passwordConfirm()).isEqualTo("pass");
    }

    @Test @DisplayName("mismatched passwords are detectable by comparison")
    void mismatchDetectable() {
        RegisterRequest req = new RegisterRequest("alice", "alice@test.com", "pass", "different");
        assertThat(req.password()).isNotEqualTo(req.passwordConfirm());
    }

    @Test @DisplayName("two instances with same values are equal")
    void equalsAndHashCode() {
        RegisterRequest a = new RegisterRequest("a", "a@a.com", "p", "p");
        RegisterRequest b = new RegisterRequest("a", "a@a.com", "p", "p");
        assertThat(a).isEqualTo(b);
    }
}
