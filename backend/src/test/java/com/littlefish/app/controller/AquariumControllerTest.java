package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.service.AquariumService;
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
//  AquariumControllerTest
// ============================================================
@WebMvcTest(AquariumController.class)
@Import(SecurityConfigTest.class)
@DisplayName("AquariumController")
class AquariumControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean AquariumService aquariumService;

    private Aquarium aquarium;

    @BeforeEach
    void setUp() {
        aquarium = new Aquarium();
        aquarium.setId(1L);
        aquarium.setName("Alice's Reef");
        aquarium.setPublic(true);
        aquarium.setLevel(2);
        aquarium.setCapacity(10);
        aquarium.setFish(new ArrayList<>());
    }

    @Nested @DisplayName("GET /api/aquariums")
    class GetAll {

        @Test @DisplayName("returns 200 with list of aquariums")
        void returnsList() throws Exception {
            when(aquariumService.findAll()).thenReturn(List.of(aquarium));

            mockMvc.perform(get("/api/aquariums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alice's Reef")));
        }
    }

    @Nested @DisplayName("GET /api/aquariums/{id}")
    class GetById {

        @Test @DisplayName("returns 200 when found")
        void found() throws Exception {
            when(aquariumService.findById(1L)).thenReturn(Optional.of(aquarium));

            mockMvc.perform(get("/api/aquariums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level", is(2)))
                .andExpect(jsonPath("$.capacity", is(10)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(aquariumService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/aquariums/99"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /api/aquariums")
    class Create {

        @Test @DisplayName("returns 200 with created aquarium")
        void creates() throws Exception {
            when(aquariumService.save(any())).thenReturn(aquarium);

            mockMvc.perform(post("/api/aquariums")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(aquarium)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice's Reef")));
        }
    }

    @Nested @DisplayName("PUT /api/aquariums/{id}")
    class Update {

        @Test @DisplayName("returns 200 with updated aquarium")
        void success() throws Exception {
            aquarium.setLevel(3);
            when(aquariumService.update(eq(1L), any())).thenReturn(Optional.of(aquarium));

            mockMvc.perform(put("/api/aquariums/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(aquarium)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level", is(3)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(aquariumService.update(eq(99L), any())).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/aquariums/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(aquarium)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("PUT /api/aquariums/{id}/visibility")
    class UpdateVisibility {

        @Test @DisplayName("makes aquarium public")
        void makesPublic() throws Exception {
            aquarium.setPublic(true);
            when(aquariumService.switchVisibility(1L, true)).thenReturn(Optional.of(aquarium));

            mockMvc.perform(put("/api/aquariums/1/visibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.public", is(true)));
        }

        @Test @DisplayName("returns 404 when aquarium not found")
        void notFound() throws Exception {
            when(aquariumService.switchVisibility(99L, false)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/aquariums/99/visibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("false"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("DELETE /api/aquariums/{id}")
    class Delete {

        @Test @DisplayName("returns 204 No Content")
        void deletes() throws Exception {
            doNothing().when(aquariumService).deleteById(1L);

            mockMvc.perform(delete("/api/aquariums/1"))
                .andExpect(status().isNoContent());

            verify(aquariumService).deleteById(1L);
        }
    }
}
