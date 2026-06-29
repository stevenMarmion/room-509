package com.littlefish.app.dto;

import com.littlefish.app.model.enums.FriendStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FriendshipDTO")
class FriendshipDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        LocalDateTime now = LocalDateTime.now();
        FriendshipDTO dto = new FriendshipDTO(FriendStatus.ACCEPTED, now, "bob", now, "USER");

        assertThat(dto.status()).isEqualTo(FriendStatus.ACCEPTED);
        assertThat(dto.since()).isEqualTo(now);
        assertThat(dto.pseudo()).isEqualTo("bob");
        assertThat(dto.friendSince()).isEqualTo(now);
        assertThat(dto.role()).isEqualTo("USER");
    }

    @Test @DisplayName("two DTOs with same values are equal")
    void equalsAndHashCode() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        FriendshipDTO a = new FriendshipDTO(FriendStatus.ACCEPTED, now, "bob", now, "USER");
        FriendshipDTO b = new FriendshipDTO(FriendStatus.ACCEPTED, now, "bob", now, "USER");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
