package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.dto.FishCreateDTO;
import com.littlefish.app.dto.FishUpdateDTO;
import com.littlefish.app.model.Fish;
import com.littlefish.app.service.FishService;
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
//  FishControllerTest
// ============================================================
@WebMvcTest(FishController.class)
@Import(SecurityConfigTest.class)
@DisplayName("FishController")
class FishControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean FishService fishService;

    private Fish nemo;

    @BeforeEach
    void setUp() {
        nemo = new Fish();
        nemo.setId(1L);
        nemo.setName("Nemo");
        nemo.setSpecies("Clownfish");
        nemo.setColor("Orange");
        nemo.setPrice(50);
        nemo.setSize(2);
        nemo.setAge(1);
        nemo.setLifePoints(100);
    }

    @Nested @DisplayName("GET /api/fish")
    class GetAll {

        @Test @DisplayName("returns 200 with fish list")
        void returnsList() throws Exception {
            when(fishService.findAll()).thenReturn(List.of(nemo));

            mockMvc.perform(get("/api/fish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Nemo")));
        }
    }

    @Nested @DisplayName("GET /api/fish/{id}")
    class GetById {

        @Test @DisplayName("returns 200 when fish found")
        void found() throws Exception {
            when(fishService.findById(1L)).thenReturn(Optional.of(nemo));

            mockMvc.perform(get("/api/fish/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nemo")))
                .andExpect(jsonPath("$.lifePoints", is(100)));
        }

        @Test @DisplayName("returns 404 when fish not found")
        void notFound() throws Exception {
            when(fishService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/fish/99"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /api/fish")
    class Create {

        @Test @DisplayName("returns 201 when creation succeeds")
        void success() throws Exception {
            when(fishService.create(any())).thenReturn(Optional.of(nemo));

            FishCreateDTO dto = new FishCreateDTO("Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 1L);

            mockMvc.perform(post("/api/fish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Nemo")));
        }

        @Test @DisplayName("returns 400 when aquarium not found")
        void badRequest() throws Exception {
            when(fishService.create(any())).thenReturn(Optional.empty());

            FishCreateDTO dto = new FishCreateDTO("Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 99L);

            mockMvc.perform(post("/api/fish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested @DisplayName("PUT /api/fish/{id}")
    class Update {

        @Test @DisplayName("returns 200 with updated fish")
        void success() throws Exception {
            nemo.setName("Nemo Updated");
            when(fishService.update(eq(1L), any())).thenReturn(Optional.of(nemo));

            FishUpdateDTO patch = new FishUpdateDTO("Nemo Updated", null, null, 0, 0, 0, 0, null, null);

            mockMvc.perform(put("/api/fish/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nemo Updated")));
        }

        @Test @DisplayName("returns 404 when fish not found")
        void notFound() throws Exception {
            when(fishService.update(eq(99L), any())).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/fish/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        new FishUpdateDTO(null, null, null, 0, 0, 0, 0, null, null))))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("DELETE /api/fish/{id}")
    class Delete {

        @Test @DisplayName("returns 204 No Content")
        void deleteFish() throws Exception {
            doNothing().when(fishService).deleteById(1L);

            mockMvc.perform(delete("/api/fish/1"))
                .andExpect(status().isNoContent());

            verify(fishService).deleteById(1L);
        }
    }
}
