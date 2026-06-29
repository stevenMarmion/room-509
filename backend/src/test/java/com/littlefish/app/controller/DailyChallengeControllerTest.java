package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.model.DailyChallenge;
import com.littlefish.app.model.DailyChallengeUser;
import com.littlefish.app.service.DailyChallengeService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ============================================================
//  DailyChallengeControllerTest
// ============================================================
@WebMvcTest(DailyChallengeController.class)
@Import(SecurityConfigTest.class)
@DisplayName("DailyChallengeController")
class DailyChallengeControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean DailyChallengeService dailyChallengeService;

    private DailyChallenge challenge;

    @BeforeEach
    void setUp() {
        challenge = new DailyChallenge();
        challenge.setId(1L);
        challenge.setName("Morning Feed");
        challenge.setReward(20);
        challenge.setDescription("Feed all fish");
        challenge.setUserEntries(new ArrayList<>());
    }

    @Nested @DisplayName("GET /api/challenges")
    class GetAll {
        @Test @DisplayName("returns 200 with list of challenges")
        void returnsList() throws Exception {
            when(dailyChallengeService.findAll()).thenReturn(List.of(challenge));

            mockMvc.perform(get("/api/challenges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Morning Feed")));
        }
    }

    @Nested @DisplayName("GET /api/challenges/{id}")
    class GetById {
        @Test @DisplayName("returns 200 when found")
        void found() throws Exception {
            when(dailyChallengeService.findById(1L)).thenReturn(Optional.of(challenge));

            mockMvc.perform(get("/api/challenges/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reward", is(20)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(dailyChallengeService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/challenges/99"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("GET /api/challenges/random")
    class GetRandom {
        @Test @DisplayName("returns 200 with up to 3 random challenges")
        void returnsRandom() throws Exception {
            when(dailyChallengeService.findRandom(3)).thenReturn(List.of(challenge));

            mockMvc.perform(get("/api/challenges/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested @DisplayName("GET /api/challenges/user/{userId}")
    class GetByUser {
        @Test @DisplayName("returns 200 with user's challenges")
        void returnsByUser() throws Exception {
            when(dailyChallengeService.findByUser(1L)).thenReturn(List.of());

            mockMvc.perform(get("/api/challenges/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested @DisplayName("POST /api/challenges/{challengeId}/complete/{userId}")
    class Complete {
        @Test @DisplayName("returns 200 with completed entry")
        void completes() throws Exception {
            DailyChallengeUser entry = new DailyChallengeUser();
            entry.setCompleted(true);
            when(dailyChallengeService.completeChallenge(1L, 1L)).thenReturn(Optional.of(entry));

            mockMvc.perform(post("/api/challenges/1/complete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));
        }

        @Test @DisplayName("returns 404 when challenge-user entry not found")
        void notFound() throws Exception {
            when(dailyChallengeService.completeChallenge(99L, 99L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/challenges/99/complete/99"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /api/challenges")
    class Create {
        @Test @DisplayName("returns 200 with created challenge")
        void creates() throws Exception {
            when(dailyChallengeService.save(any())).thenReturn(challenge);

            mockMvc.perform(post("/api/challenges")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(challenge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Morning Feed")));
        }
    }

    @Nested @DisplayName("PUT /api/challenges/{id}")
    class Update {
        @Test @DisplayName("returns 200 with updated challenge")
        void updates() throws Exception {
            challenge.setReward(50);
            when(dailyChallengeService.update(eq(1L), any())).thenReturn(Optional.of(challenge));

            mockMvc.perform(put("/api/challenges/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(challenge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reward", is(50)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(dailyChallengeService.update(eq(99L), any())).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/challenges/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(challenge)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("DELETE /api/challenges/{id}")
    class Delete {
        @Test @DisplayName("returns 204 No Content")
        void deletes() throws Exception {
            doNothing().when(dailyChallengeService).deleteById(1L);

            mockMvc.perform(delete("/api/challenges/1"))
                .andExpect(status().isNoContent());

            verify(dailyChallengeService).deleteById(1L);
        }
    }
}
