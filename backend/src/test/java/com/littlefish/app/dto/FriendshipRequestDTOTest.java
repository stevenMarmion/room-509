package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FriendshipRequestDTO")
class FriendshipRequestDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        LocalDateTime now = LocalDateTime.now();
        FriendshipRequestDTO dto = new FriendshipRequestDTO(1L, "PENDING", now, "alice", "bob");
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.status()).isEqualTo("PENDING");
        assertThat(dto.since()).isEqualTo(now);
        assertThat(dto.senderUsername()).isEqualTo("alice");
        assertThat(dto.receiverUsername()).isEqualTo("bob");
    }

    @Test @DisplayName("two instances with same values are equal")
    void equalsAndHashCode() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 1, 0, 0);
        FriendshipRequestDTO a = new FriendshipRequestDTO(1L, "PENDING", now, "alice", "bob");
        FriendshipRequestDTO b = new FriendshipRequestDTO(1L, "PENDING", now, "alice", "bob");
        assertThat(a).isEqualTo(b);
    }
}
