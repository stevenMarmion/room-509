package com.littlefish.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.dto.CreateTradeRequest;
import com.littlefish.app.dto.TradeDTO;
import com.littlefish.app.model.Trade;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.TradeStatus;
import com.littlefish.app.service.TradeService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ============================================================
//  TradeControllerTest
// ============================================================
@WebMvcTest(TradeController.class)
@Import(SecurityConfigTest.class)
@DisplayName("TradeController")
class TradeControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean TradeService tradeService;

    private Trade trade;
    private TradeDTO tradeDTO;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = new User(); alice.setId(1L); alice.setPseudo("alice");
        bob   = new User(); bob.setId(2L);   bob.setPseudo("bob");

        trade = new Trade();
        trade.setId(1L);
        trade.setInitiator(alice);
        trade.setReceiver(bob);
        trade.setPrice(80);
        trade.setStatus(TradeStatus.PENDING);
        trade.setCreatedAt(LocalDateTime.of(2026, 6, 25, 9, 0));
        trade.setFish(new ArrayList<>());

        tradeDTO = new TradeDTO(1L, TradeStatus.PENDING,
            LocalDateTime.of(2026, 6, 25, 9, 0), 80, "alice", "bob", List.of());
    }

    @Nested @DisplayName("GET /api/trades")
    class GetAll {
        @Test @DisplayName("returns 200 with list of trades")
        void returnsList() throws Exception {
            when(tradeService.findAll()).thenReturn(List.of(trade));

            mockMvc.perform(get("/api/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
        }
    }

    @Nested @DisplayName("GET /api/trades/{id}")
    class GetById {
        @Test @DisplayName("returns 200 when found")
        void found() throws Exception {
            when(tradeService.findById(1L)).thenReturn(Optional.of(trade));

            mockMvc.perform(get("/api/trades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", is(80)));
        }

        @Test @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            when(tradeService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/trades/99"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("GET /api/trades/user/{pseudo}")
    class GetByPseudo {
        @Test @DisplayName("returns 200 with trades for user")
        void returnsTrades() throws Exception {
            when(tradeService.findByPseudo("alice")).thenReturn(List.of(tradeDTO));

            mockMvc.perform(get("/api/trades/user/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].initiatorPseudo", is("alice")));
        }
    }

    @Nested @DisplayName("POST /api/trades")
    class Create {
        @Test @DisplayName("returns 201 with created trade DTO")
        void creates() throws Exception {
            when(tradeService.createTrade(any())).thenReturn(tradeDTO);

            CreateTradeRequest req = new CreateTradeRequest("alice", 2L, 80, List.of());

            mockMvc.perform(post("/api/trades")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.initiatorPseudo", is("alice")))
                .andExpect(jsonPath("$.receiverPseudo", is("bob")));
        }
    }

    @Nested @DisplayName("POST /api/trades/{id}/accept")
    class AcceptTrade {
        @Test @DisplayName("returns 200 with accepted trade")
        void accepts() throws Exception {
            trade.setStatus(TradeStatus.ACCEPTED);
            when(tradeService.acceptTrade(1L)).thenReturn(Optional.of(trade));

            mockMvc.perform(post("/api/trades/1/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
        }

        @Test @DisplayName("returns 404 when trade not found")
        void notFound() throws Exception {
            when(tradeService.acceptTrade(99L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/trades/99/accept"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /api/trades/{id}/reject")
    class RejectTrade {
        @Test @DisplayName("returns 200 with rejected trade")
        void rejects() throws Exception {
            trade.setStatus(TradeStatus.REJECTED);
            when(tradeService.rejectTrade(1L)).thenReturn(Optional.of(trade));

            mockMvc.perform(post("/api/trades/1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));
        }
    }

    @Nested @DisplayName("DELETE /api/trades/{id}")
    class Delete {
        @Test @DisplayName("returns 204 No Content")
        void deletes() throws Exception {
            doNothing().when(tradeService).deleteById(1L);

            mockMvc.perform(delete("/api/trades/1"))
                .andExpect(status().isNoContent());

            verify(tradeService).deleteById(1L);
        }
    }
}