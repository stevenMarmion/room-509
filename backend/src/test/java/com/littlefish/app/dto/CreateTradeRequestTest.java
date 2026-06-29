package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateTradeRequest")
class CreateTradeRequestTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        CreateTradeRequest req = new CreateTradeRequest("alice", 2L, 80, List.of(10L, 11L));
        assertThat(req.initiatorPseudo()).isEqualTo("alice");
        assertThat(req.receiverId()).isEqualTo(2L);
        assertThat(req.price()).isEqualTo(80);
        assertThat(req.fishIds()).containsExactly(10L, 11L);
    }

    @Test @DisplayName("two instances with same values are equal")
    void equalsAndHashCode() {
        CreateTradeRequest a = new CreateTradeRequest("alice", 2L, 80, List.of(10L));
        CreateTradeRequest b = new CreateTradeRequest("alice", 2L, 80, List.of(10L));
        assertThat(a).isEqualTo(b);
    }

    @Test @DisplayName("empty fish list is valid")
    void emptyFishList() {
        CreateTradeRequest req = new CreateTradeRequest("alice", 2L, 0, List.of());
        assertThat(req.fishIds()).isEmpty();
    }
}
