package com.littlefish.app.dto;

import com.littlefish.app.model.Fish;
import com.littlefish.app.model.enums.TradeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TradeDTO")
class TradeDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        LocalDateTime now = LocalDateTime.now();
        Fish fish = new Fish(); fish.setName("Nemo");
        TradeDTO dto = new TradeDTO(1L, TradeStatus.PENDING, now, 80, "alice", "bob", List.of(fish));

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.status()).isEqualTo(TradeStatus.PENDING);
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.price()).isEqualTo(80);
        assertThat(dto.initiatorPseudo()).isEqualTo("alice");
        assertThat(dto.receiverPseudo()).isEqualTo("bob");
        assertThat(dto.fish()).hasSize(1);
    }

    @Test @DisplayName("two DTOs with same values are equal")
    void equalsAndHashCode() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 25, 10, 0);
        TradeDTO a = new TradeDTO(1L, TradeStatus.PENDING, now, 50, "alice", "bob", List.of());
        TradeDTO b = new TradeDTO(1L, TradeStatus.PENDING, now, 50, "alice", "bob", List.of());
        assertThat(a).isEqualTo(b);
    }
}
