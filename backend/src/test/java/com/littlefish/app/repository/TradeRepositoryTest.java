package com.littlefish.app.repository;

import com.littlefish.app.model.*;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.model.enums.TradeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TradeRepository")
class TradeRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired TradeRepository tradeRepository;

    private User alice;
    private User bob;
    private User charlie;

    @BeforeEach
    void setUp() {
        alice   = persistUser("alice",   "alice@test.com");
        bob     = persistUser("bob",     "bob@test.com");
        charlie = persistUser("charlie", "charlie@test.com");
    }

    private User persistUser(String pseudo, String email) {
        User u = new User();
        u.setPseudo(pseudo); u.setEmail(email); u.setPassword("hashed");
        u.setCoins(200); u.setRole(Role.USER); u.setTheme(Theme.LIGHT);
        u.setCreatedAt(LocalDateTime.now());
        u.setFriendships(new ArrayList<>()); u.setTrades(new ArrayList<>());
        u.setDailyChallengeEntries(new ArrayList<>());

        NotificationPreference pref = new NotificationPreference();
        pref.setUser(u); u.setNotificationPreference(pref);

        Aquarium aq = new Aquarium();
        aq.setName(pseudo + "'s Tank"); aq.setLevel(1); aq.setCapacity(5);
        aq.setPublic(false); aq.setUser(u); aq.setFish(new ArrayList<>());
        u.setAquarium(aq);

        return em.persistAndFlush(u);
    }

    private Trade persistTrade(User initiator, User receiver, TradeStatus status) {
        Trade t = new Trade();
        t.setInitiator(initiator);
        t.setReceiver(receiver);
        t.setPrice(50);
        t.setStatus(status);
        t.setCreatedAt(LocalDateTime.now());
        t.setFish(new ArrayList<>());
        return em.persistAndFlush(t);
    }

    @Nested @DisplayName("findByInitiator_PseudoOrReceiver_Pseudo")
    class FindByPseudo {

        @Test @DisplayName("returns trades where user is initiator")
        void asInitiator() {
            persistTrade(alice, bob, TradeStatus.PENDING);

            List<Trade> result = tradeRepository
                .findByInitiator_PseudoOrReceiver_Pseudo("alice", "alice");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getInitiator().getPseudo()).isEqualTo("alice");
        }

        @Test @DisplayName("returns trades where user is receiver")
        void asReceiver() {
            persistTrade(bob, alice, TradeStatus.ACCEPTED);

            List<Trade> result = tradeRepository
                .findByInitiator_PseudoOrReceiver_Pseudo("alice", "alice");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getReceiver().getPseudo()).isEqualTo("alice");
        }

        @Test @DisplayName("returns both sides when user is involved in multiple trades")
        void bothSides() {
            persistTrade(alice, bob,     TradeStatus.PENDING);  // alice as initiator
            persistTrade(charlie, alice, TradeStatus.ACCEPTED); // alice as receiver

            List<Trade> result = tradeRepository
                .findByInitiator_PseudoOrReceiver_Pseudo("alice", "alice");

            assertThat(result).hasSize(2);
        }

        @Test @DisplayName("returns empty when user has no trades")
        void noTrades() {
            List<Trade> result = tradeRepository
                .findByInitiator_PseudoOrReceiver_Pseudo("charlie", "charlie");

            assertThat(result).isEmpty();
        }

        @Test @DisplayName("returns all statuses — does not filter by status")
        void allStatuses() {
            persistTrade(alice, bob, TradeStatus.PENDING);
            persistTrade(alice, bob, TradeStatus.ACCEPTED);
            persistTrade(alice, bob, TradeStatus.REJECTED);

            List<Trade> result = tradeRepository
                .findByInitiator_PseudoOrReceiver_Pseudo("alice", "alice");

            assertThat(result).hasSize(3);
            assertThat(result).extracting(Trade::getStatus)
                .containsExactlyInAnyOrder(
                    TradeStatus.PENDING, TradeStatus.ACCEPTED, TradeStatus.REJECTED);
        }
    }

    @Nested @DisplayName("save / findById / deleteById")
    class CrudBasics {

        @Test @DisplayName("save persists trade and assigns id")
        void saves() {
            Trade t = persistTrade(alice, bob, TradeStatus.PENDING);
            assertThat(t.getId()).isNotNull();
        }

        @Test @DisplayName("findById returns persisted trade")
        void findById() {
            Trade t = persistTrade(alice, bob, TradeStatus.PENDING);
            assertThat(tradeRepository.findById(t.getId())).isPresent();
        }

        @Test @DisplayName("deleteById removes trade")
        void deleteById() {
            Trade t = persistTrade(alice, bob, TradeStatus.PENDING);
            Long id = t.getId();

            tradeRepository.deleteById(id);
            em.flush();

            assertThat(tradeRepository.findById(id)).isEmpty();
        }
    }
}
