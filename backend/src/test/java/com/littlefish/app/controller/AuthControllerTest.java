package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.dto.LoginRequest;
import com.littlefish.app.dto.RegisterRequest;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.service.JwtService;
import com.littlefish.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfigTest.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean UserService userService;
    @MockitoBean JwtService jwtService;
    @MockitoBean PasswordEncoder passwordEncoder;

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setId(1L);
        alice.setPseudo("alice");
        alice.setEmail("alice@test.com");
        alice.setPassword("$2a$10$hashedpassword");
        alice.setCoins(200);
        alice.setRole(Role.USER);
        alice.setTheme(Theme.LIGHT);
        alice.setCreatedAt(LocalDateTime.now());
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("POST /api/auth/register")
    class Register {

        @Test @DisplayName("returns 201 on successful registration")
        void success() throws Exception {
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
            when(userService.registerUser(any())).thenReturn(alice);
            when(jwtService.generateToken("alice")).thenReturn("jwt.token.here");

            RegisterRequest req = new RegisterRequest("alice", "alice@test.com", "pass123", "pass123");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pseudo", is("alice")))
                .andExpect(cookie().exists("auth_token"))
                .andExpect(cookie().httpOnly("auth_token", true));
        }

        @Test @DisplayName("returns 400 when passwords do not match")
        void passwordMismatch() throws Exception {
            RegisterRequest req = new RegisterRequest("alice", "alice@test.com", "pass123", "different");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }

        @Test @DisplayName("returns 400 when pseudo is already taken")
        void pseudoAlreadyTaken() throws Exception {
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
            when(userService.registerUser(any())).thenReturn(null);

            RegisterRequest req = new RegisterRequest("alice", "other@test.com", "pass123", "pass123");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("POST /api/auth/login")
    class Login {

        @Test @DisplayName("returns 200 with user and sets auth cookie on valid credentials")
        void success() throws Exception {
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(passwordEncoder.matches("pass123", alice.getPassword())).thenReturn(true);
            when(jwtService.generateToken("alice")).thenReturn("jwt.token.here");

            LoginRequest req = new LoginRequest("alice", "pass123");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo", is("alice")))
                .andExpect(cookie().exists("auth_token"))
                .andExpect(cookie().httpOnly("auth_token", true));
        }

        @Test @DisplayName("returns 401 when user not found")
        void userNotFound() throws Exception {
            when(userService.findByPseudo("ghost")).thenReturn(Optional.empty());

            LoginRequest req = new LoginRequest("ghost", "pass");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }

        @Test @DisplayName("returns 401 when password is wrong")
        void wrongPassword() throws Exception {
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(passwordEncoder.matches("wrong", alice.getPassword())).thenReturn(false);

            LoginRequest req = new LoginRequest("alice", "wrong");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("POST /api/auth/logout")
    class Logout {

        @Test @DisplayName("returns 204 and clears the auth cookie")
        void clearsAuthCookie() throws Exception {
            mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("auth_token"))
                .andExpect(cookie().maxAge("auth_token", 0));
        }
    }
}
