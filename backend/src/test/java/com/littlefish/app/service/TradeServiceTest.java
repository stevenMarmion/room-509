package com.littlefish.app.service;

import com.littlefish.app.dto.CreateTradeRequest;
import com.littlefish.app.dto.TradeDTO;
import com.littlefish.app.model.*;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.model.enums.TradeStatus;
import com.littlefish.app.repository.FishRepository;
import com.littlefish.app.repository.TradeRepository;
import com.littlefish.app.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TradeService")
class TradeServiceTest {

    @Mock TradeRepository tradeRepository;
    @Mock UserRepository userRepository;
    @Mock FishRepository fishRepository;
    @Mock UserService userService;
    @Mock FishService fishService;

    @InjectMocks TradeService tradeService;

    private User makeUser(Long id, String pseudo, int coins) {
        User u = new User();
        u.setId(id);
        u.setPseudo(pseudo);
        u.setCoins(coins);
        u.setRole(Role.USER);
        u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        Aquarium aq = new Aquarium();
        aq.setId(id);
        aq.setFish(new ArrayList<>());
        u.setAquarium(aq);
        return u;
    }

    private Fish makeFish(Long id, String name) {
        Fish f = new Fish();
        f.setId(id);
        f.setName(name);
        f.setSpecies("Clownfish");
        f.setLifePoints(100);
        return f;
    }

    private Trade makeTrade(Long id, User initiator, User receiver, List<Fish> fish, int price, TradeStatus status) {
        Trade t = new Trade();
        t.setId(id);
        t.setInitiator(initiator);
        t.setReceiver(receiver);
        t.setFish(new ArrayList<>(fish));
        t.setPrice(price);
        t.setStatus(status);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("createTrade")
    class CreateTrade {

        @Test @DisplayName("creates a pending trade with correct fields")
        void success() {
            User alice = makeUser(1L, "alice", 200);
            User bob   = makeUser(2L, "bob",   100);
            Fish fish  = makeFish(10L, "Nemo");

            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(userService.findById(2L)).thenReturn(Optional.of(bob));
            when(fishService.findById(10L)).thenReturn(Optional.of(fish));
            when(tradeRepository.save(any())).thenAnswer(inv -> {
                Trade t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });

            CreateTradeRequest req = new CreateTradeRequest("alice", 2L, 80, List.of(10L));
            TradeDTO dto = tradeService.createTrade(req);

            assertThat(dto.status()).isEqualTo(TradeStatus.PENDING);
            assertThat(dto.initiatorPseudo()).isEqualTo("alice");
            assertThat(dto.receiverPseudo()).isEqualTo("bob");
            assertThat(dto.price()).isEqualTo(80);
            assertThat(dto.fish()).hasSize(1);
        }

        @Test @DisplayName("skips invalid fish IDs gracefully")
        void skipsInvalidFishIds() {
            User alice = makeUser(1L, "alice", 200);
            User bob   = makeUser(2L, "bob",   100);

            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(userService.findById(2L)).thenReturn(Optional.of(bob));
            when(fishService.findById(99L)).thenReturn(Optional.empty());
            when(tradeRepository.save(any())).thenAnswer(inv -> {
                Trade t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });

            CreateTradeRequest req = new CreateTradeRequest("alice", 2L, 50, List.of(99L));
            TradeDTO dto = tradeService.createTrade(req);

            assertThat(dto.fish()).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("acceptTrade")
    class AcceptTrade {

        @Test @DisplayName("transfers fish and adjusts coins on acceptance")
        void transfersFishAndCoins() {
            User alice = makeUser(1L, "alice", 200);
            User bob   = makeUser(2L, "bob",   100);

            Fish fish = makeFish(10L, "Nemo");
            fish.setAquarium(alice.getAquarium());
            alice.getAquarium().getFish().add(fish);

            Trade trade = makeTrade(1L, alice, bob, List.of(fish), 60, TradeStatus.PENDING);

            when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
            when(userService.findByPseudo("alice")).thenReturn(Optional.of(alice));
            when(userService.findByPseudo("bob")).thenReturn(Optional.of(bob));
            when(tradeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<Trade> result = tradeService.acceptTrade(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(TradeStatus.ACCEPTED);

            // Alice receives coins, bob pays
            verify(userRepository).updateCoins("alice", 260);  // 200 + 60
            verify(userRepository).updateCoins("bob",    40);  // 100 - 60

            // Fish moved to bob's aquarium
            assertThat(fish.getAquarium()).isEqualTo(bob.getAquarium());
        }

        @Test @DisplayName("returns empty when trade not found")
        void tradeNotFound() {
            when(tradeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(tradeService.acceptTrade(99L)).isEmpty();
        }

        @Test @DisplayName("returns empty when a user is missing")
        void userMissing() {
            User alice = makeUser(1L, "alice", 200);
            User bob   = makeUser(2L, "bob",   100);
            Trade trade = makeTrade(1L, alice, bob, List.of(), 50, TradeStatus.PENDING);

            when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
            when(userService.findByPseudo("alice")).thenReturn(Optional.empty());

            assertThat(tradeService.acceptTrade(1L)).isEmpty();
            verify(userRepository, never()).updateCoins(any(), anyInt());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("rejectTrade")
    class RejectTrade {

        @Test @DisplayName("sets trade status to REJECTED")
        void rejectsTrade() {
            User alice = makeUser(1L, "alice", 200);
            User bob   = makeUser(2L, "bob",   100);
            Trade trade = makeTrade(1L, alice, bob, List.of(), 50, TradeStatus.PENDING);

            when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));
            when(tradeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<Trade> result = tradeService.rejectTrade(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(TradeStatus.REJECTED);
        }

        @Test @DisplayName("returns empty when trade not found")
        void tradeNotFound() {
            when(tradeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(tradeService.rejectTrade(99L)).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("findByPseudo")
    class FindByPseudo {
        @Test @DisplayName("returns trades where user is initiator or receiver")
        void returnsBothSides() {
            User alice = makeUser(1L, "alice", 100);
            User bob   = makeUser(2L, "bob",   100);

            Trade t1 = makeTrade(1L, alice, bob, List.of(), 50, TradeStatus.PENDING);
            Trade t2 = makeTrade(2L, bob, alice, List.of(), 30, TradeStatus.ACCEPTED);

            when(tradeRepository.findByInitiator_PseudoOrReceiver_Pseudo("alice", "alice"))
                .thenReturn(List.of(t1, t2));

            List<TradeDTO> results = tradeService.findByPseudo("alice");

            assertThat(results).hasSize(2);
        }
    }
}
