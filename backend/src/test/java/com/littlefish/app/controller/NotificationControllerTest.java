package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.dto.NotificationPreferenceUpdateDTO;
import com.littlefish.app.model.NotificationPreference;
import com.littlefish.app.model.User;
import com.littlefish.app.service.NotificationPreferenceService;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// ============================================================
//  NotificationPreferenceControllerTest
// ============================================================
@WebMvcTest(NotificationPreferenceController.class)
@Import(SecurityConfigTest.class)
@DisplayName("NotificationPreferenceController")
class NotificationPreferenceControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean NotificationPreferenceService notificationPreferenceService;

    private NotificationPreference pref;
    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User(); alice.setId(1L); alice.setPseudo("alice");

        pref = new NotificationPreference();
        pref.setId(1L);
        pref.setUser(alice);
        pref.setNotifyOnDeath(true);
        pref.setNotifyBeforeDeath(true);
        pref.setDailyReminder(false);
    }

    @Nested @DisplayName("GET /api/notifications")
    class GetAll {
        @Test @DisplayName("returns 200 with list of preferences as DTOs")
        void returnsList() throws Exception {
            when(notificationPreferenceService.findAll()).thenReturn(List.of(pref));

            mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pseudo", is("alice")))
                .andExpect(jsonPath("$[0].notifyOnDeath", is(true)));
        }
    }

    @Nested @DisplayName("GET /api/notifications/{id}")
    class GetById {
        @Test @DisplayName("returns 200 with preference DTO")
        void found() throws Exception {
            when(notificationPreferenceService.findById(1L)).thenReturn(Optional.of(pref));

            mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo", is("alice")))
                .andExpect(jsonPath("$.dailyReminder", is(false)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(notificationPreferenceService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/notifications/99"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("PUT /api/notifications/{id}")
    class Update {
        @Test @DisplayName("returns 200 with updated preference DTO")
        void updates() throws Exception {
            pref.setDailyReminder(true);
            when(notificationPreferenceService.update(eq(1L), any())).thenReturn(Optional.of(pref));

            NotificationPreferenceUpdateDTO dto =
                new NotificationPreferenceUpdateDTO(null, null, true, "alice");

            mockMvc.perform(put("/api/notifications/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyReminder", is(true)));
        }

        @Test @DisplayName("returns 404 when preference not found")
        void notFound() throws Exception {
            when(notificationPreferenceService.update(eq(99L), any())).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/notifications/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        new NotificationPreferenceUpdateDTO(null, null, null, "ghost"))))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("DELETE /api/notifications/{id}")
    class Delete {
        @Test @DisplayName("returns 204 No Content")
        void deletes() throws Exception {
            doNothing().when(notificationPreferenceService).deleteById(1L);

            mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());

            verify(notificationPreferenceService).deleteById(1L);
        }
    }
}
