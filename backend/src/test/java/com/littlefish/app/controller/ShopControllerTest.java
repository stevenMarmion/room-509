package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.AquariumUpgrade;
import com.littlefish.app.model.Fish;
import com.littlefish.app.model.Food;
import com.littlefish.app.model.User;
import com.littlefish.app.service.ShopService;
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
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ============================================================
//  ShopControllerTest
// ============================================================
@WebMvcTest(ShopController.class)
@Import(SecurityConfigTest.class)
@DisplayName("ShopController")
class ShopControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean ShopService shopService;

    private Food food;
    private AquariumUpgrade upgrade;
    private Fish fishTemplate;
    private User alice;

    @BeforeEach
    void setUp() {
        food = new Food();
        food.setId(1L);
        food.setName("Premium Flakes");
        food.setPrice(25);
        food.setNutritionValue(30);

        upgrade = new AquariumUpgrade();
        upgrade.setId(1L);
        upgrade.setName("Small Extension");
        upgrade.setPrice(100);
        upgrade.setCapacityBonus(5);
        upgrade.setLevelBonus(0);

        fishTemplate = new Fish();
        fishTemplate.setId(1L);
        fishTemplate.setName("Nemo");
        fishTemplate.setSpecies("Clownfish");
        fishTemplate.setPrice(50);

        Aquarium aq = new Aquarium();
        aq.setId(1L);
        aq.setFish(new ArrayList<>());
        aq.setCapacity(10);

        alice = new User();
        alice.setId(1L);
        alice.setPseudo("alice");
        alice.setCoins(200);
        alice.setAquarium(aq);
    }

    // ── Food ──────────────────────────────────────────────────────

    @Nested @DisplayName("GET /api/shop/food")
    class GetAllFood {
        @Test @DisplayName("returns 200 with food list")
        void returnsList() throws Exception {
            when(shopService.findAllFood()).thenReturn(List.of(food));

            mockMvc.perform(get("/api/shop/food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Premium Flakes")));
        }
    }

    @Nested @DisplayName("GET /api/shop/food/{id}")
    class GetFoodById {
        @Test @DisplayName("returns 200 when found")
        void found() throws Exception {
            when(shopService.findFoodById(1L)).thenReturn(Optional.of(food));

            mockMvc.perform(get("/api/shop/food/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nutritionValue", is(30)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(shopService.findFoodById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/shop/food/99"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /api/shop/food/{id}/buy")
    class BuyFood {
        @Test @DisplayName("returns 200 when purchase succeeds")
        void success() throws Exception {
            when(shopService.buyFood(1L, "alice")).thenReturn(Optional.of(alice));

            mockMvc.perform(post("/api/shop/food/1/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userId", "alice"))))
                .andExpect(status().isOk());
        }

        @Test @DisplayName("returns 400 when not enough coins")
        void notEnoughCoins() throws Exception {
            when(shopService.buyFood(1L, "alice")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/shop/food/1/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userId", "alice"))))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested @DisplayName("POST /api/shop/food")
    class CreateFood {
        @Test @DisplayName("returns 200 with created food")
        void creates() throws Exception {
            when(shopService.createFood(any())).thenReturn(food);

            mockMvc.perform(post("/api/shop/food")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(food)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Premium Flakes")));
        }
    }

    @Nested @DisplayName("PUT /api/shop/food/{id}")
    class UpdateFood {
        @Test @DisplayName("returns 200 with updated food")
        void updates() throws Exception {
            food.setPrice(30);
            when(shopService.updateFood(eq(1L), any())).thenReturn(Optional.of(food));

            mockMvc.perform(put("/api/shop/food/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(food)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", is(30)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(shopService.updateFood(eq(99L), any())).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/shop/food/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(food)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("DELETE /api/shop/food/{id}")
    class DeleteFood {
        @Test @DisplayName("returns 204 No Content")
        void deletes() throws Exception {
            doNothing().when(shopService).deleteFood(1L);

            mockMvc.perform(delete("/api/shop/food/1"))
                .andExpect(status().isNoContent());

            verify(shopService).deleteFood(1L);
        }
    }

    // ── Upgrades ──────────────────────────────────────────────────

    @Nested @DisplayName("GET /api/shop/upgrades")
    class GetAllUpgrades {
        @Test @DisplayName("returns 200 with upgrades list")
        void returnsList() throws Exception {
            when(shopService.findAllUpgrades()).thenReturn(List.of(upgrade));

            mockMvc.perform(get("/api/shop/upgrades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].capacityBonus", is(5)));
        }
    }

    @Nested @DisplayName("POST /api/shop/upgrades/{id}/buy")
    class BuyUpgrade {
        @Test @DisplayName("returns 200 when purchase succeeds")
        void success() throws Exception {
            when(shopService.buyUpgrade(1L, "alice")).thenReturn(Optional.of(alice));

            mockMvc.perform(post("/api/shop/upgrades/1/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userId", "alice"))))
                .andExpect(status().isOk());
        }

        @Test @DisplayName("returns 400 when at max level or insufficient coins")
        void fails() throws Exception {
            when(shopService.buyUpgrade(1L, "alice")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/shop/upgrades/1/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userId", "alice"))))
                .andExpect(status().isBadRequest());
        }
    }

    // ── Fish ──────────────────────────────────────────────────────

    @Nested @DisplayName("GET /api/shop/fish")
    class GetAllFish {
        @Test @DisplayName("returns 200 with fish templates")
        void returnsList() throws Exception {
            when(shopService.findAllFish()).thenReturn(List.of(fishTemplate));

            mockMvc.perform(get("/api/shop/fish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Nemo")));
        }
    }

    @Nested @DisplayName("POST /api/shop/fish/{id}/buy")
    class BuyFish {
        @Test @DisplayName("returns 200 when purchase succeeds")
        void success() throws Exception {
            Aquarium aq = new Aquarium();
            aq.setFish(new ArrayList<>());
            when(shopService.buyFish(1L, "alice")).thenReturn(Optional.of(aq));

            mockMvc.perform(post("/api/shop/fish/1/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userId", "alice"))))
                .andExpect(status().isOk());
        }

        @Test @DisplayName("returns 400 when aquarium is full or insufficient coins")
        void fails() throws Exception {
            when(shopService.buyFish(1L, "alice")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/shop/fish/1/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userId", "alice"))))
                .andExpect(status().isBadRequest());
        }
    }
}