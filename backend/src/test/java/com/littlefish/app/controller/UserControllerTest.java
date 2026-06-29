package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.dto.UpdateCoinsRequest;
import com.littlefish.app.dto.UpdateRoleRequest;
import com.littlefish.app.dto.UserUpdateDTO;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Fish;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfigTest.class)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean UserService userService;

    private User alice;

    @BeforeEach
    void setUp() {
        Aquarium aq = new Aquarium();
        aq.setId(1L);
        aq.setFish(new ArrayList<>(List.of(makeFish(10L, "Nemo"))));

        alice = new User();
        alice.setId(1L);
        alice.setPseudo("alice");
        alice.setEmail("alice@test.com");
        alice.setCoins(200);
        alice.setRole(Role.USER);
        alice.setTheme(Theme.LIGHT);
        alice.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        alice.setAquarium(aq);
    }

    private Fish makeFish(Long id, String name) {
        Fish f = new Fish();
        f.setId(id);
        f.setName(name);
        f.setLifePoints(100);
        return f;
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("GET /api/users")
    class GetAll {

        @Test @DisplayName("returns 200 with list of users")
        void returnsAll() throws Exception {
            when(userService.findAll()).thenReturn(List.of(alice));

            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pseudo", is("alice")));
        }

        @Test @DisplayName("returns 200 with empty list when no users")
        void emptyList() throws Exception {
            when(userService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("GET /api/users/{pseudo}")
    class GetByPseudo {

        @Test @DisplayName("returns 200 with user when found")
        void found() throws Exception {
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));

            mockMvc.perform(get("/api/users/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo", is("alice")))
                .andExpect(jsonPath("$.coins", is(200)));
        }

        @Test @DisplayName("returns 404 when user not found")
        void notFound() throws Exception {
            when(userService.findByPseudo("ghost")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/users/ghost"))
                .andExpect(status().isNotFound());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("GET /api/users/{pseudo}/fishes")
    class GetFishes {

        @Test @DisplayName("returns 200 with fish list")
        void returnsFishes() throws Exception {
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));

            mockMvc.perform(get("/api/users/alice/fishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Nemo")));
        }

        @Test @DisplayName("returns 404 when user not found")
        void userNotFound() throws Exception {
            when(userService.findByPseudo("ghost")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/users/ghost/fishes"))
                .andExpect(status().isNotFound());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("PUT /api/users/{pseudo}")
    class Update {

        @Test @DisplayName("returns 200 with updated user")
        void updatesUser() throws Exception {
            alice.setPseudo("alice2");
            when(userService.update(eq("alice"), any())).thenReturn(Optional.of(alice));

            UserUpdateDTO dto = new UserUpdateDTO("alice2", null, null, null);

            mockMvc.perform(put("/api/users/alice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo", is("alice2")));
        }

        @Test @DisplayName("returns 404 when user not found")
        void userNotFound() throws Exception {
            when(userService.update(eq("ghost"), any())).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/users/ghost")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UserUpdateDTO(null, null, null, null))))
                .andExpect(status().isNotFound());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("POST /api/users/{pseudo}/earn")
    class EarnCoins {

        @Test @DisplayName("returns 200 after adding 1 coin")
        void earnsOneCoin() throws Exception {
            alice.setCoins(201);
            when(userService.earnCoins("alice", 1)).thenReturn(Optional.of(alice));

            mockMvc.perform(post("/api/users/alice/earn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins", is(201)));
        }

        @Test @DisplayName("returns 404 when user not found")
        void userNotFound() throws Exception {
            when(userService.earnCoins("ghost", 1)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/users/ghost/earn"))
                .andExpect(status().isNotFound());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("DELETE /api/users/{pseudo}")
    class Delete {

        @Test @DisplayName("returns 204 No Content")
        void deletesUser() throws Exception {
            doNothing().when(userService).deleteByPseudo("alice");

            mockMvc.perform(delete("/api/users/alice"))
                .andExpect(status().isNoContent());

            verify(userService).deleteByPseudo("alice");
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("PATCH /api/users/{pseudo}/role")
    class UpdateRole {

        @Test @DisplayName("returns 200 after promoting to ADMIN")
        void promotesToAdmin() throws Exception {
            alice.setRole(Role.ADMIN);
            when(userService.updateRole("alice", Role.ADMIN)).thenReturn(Optional.of(alice));

            mockMvc.perform(patch("/api/users/alice/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UpdateRoleRequest(Role.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("ADMIN")));
        }

        @Test @DisplayName("returns 404 when user not found")
        void userNotFound() throws Exception {
            when(userService.updateRole("ghost", Role.ADMIN)).thenReturn(Optional.empty());

            mockMvc.perform(patch("/api/users/ghost/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UpdateRoleRequest(Role.ADMIN))))
                .andExpect(status().isNotFound());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("PATCH /api/users/{pseudo}/coins")
    class UpdateCoins {

        @Test @DisplayName("returns 200 after setting coins")
        void setsCoins() throws Exception {
            alice.setCoins(999);
            when(userService.updateCoinsAdmin("alice", 999)).thenReturn(Optional.of(alice));

            mockMvc.perform(patch("/api/users/alice/coins")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UpdateCoinsRequest(999))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins", is(999)));
        }

        @Test @DisplayName("returns 404 when user not found")
        void userNotFound() throws Exception {
            when(userService.updateCoinsAdmin("ghost", 100)).thenReturn(Optional.empty());

            mockMvc.perform(patch("/api/users/ghost/coins")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UpdateCoinsRequest(100))))
                .andExpect(status().isNotFound());
        }
    }
}
