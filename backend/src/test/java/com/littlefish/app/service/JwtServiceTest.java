package com.littlefish.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService")
class JwtServiceTest {

    // JwtService has no external dependencies — instantiate directly
    JwtService jwtService = new JwtService();

    @Test @DisplayName("generated token is non-null and non-empty")
    void generateToken_nonEmpty() {
        String token = jwtService.generateToken("alice");
        assertThat(token).isNotBlank();
    }

    @Test @DisplayName("extracted pseudo matches the one used to generate the token")
    void extractPseudo_roundTrip() {
        String token = jwtService.generateToken("alice");
        assertThat(jwtService.extractPseudo(token)).isEqualTo("alice");
    }

    @Test @DisplayName("isValid returns true for a freshly generated token")
    void isValid_trueForFreshToken() {
        String token = jwtService.generateToken("bob");
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test @DisplayName("isValid returns false for a tampered token")
    void isValid_falseForTamperedToken() {
        String token = jwtService.generateToken("alice");
        String tampered = token.substring(0, token.length() - 4) + "XXXX";
        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test @DisplayName("isValid returns false for an empty string")
    void isValid_falseForEmptyString() {
        assertThat(jwtService.isValid("")).isFalse();
    }

    @Test @DisplayName("different pseudos produce different tokens")
    void differentPseudos_differentTokens() {
        String t1 = jwtService.generateToken("alice");
        String t2 = jwtService.generateToken("bob");
        assertThat(t1).isNotEqualTo(t2);
    }
}
