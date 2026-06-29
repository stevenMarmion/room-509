package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificationPreferenceRequestDTO")
class NotificationPreferenceRequestDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        NotificationPreferenceRequestDTO dto =
            new NotificationPreferenceRequestDTO(1L, true, false, false, "alice");
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.notifyOnDeath()).isTrue();
        assertThat(dto.notifyBeforeDeath()).isFalse();
        assertThat(dto.dailyReminder()).isFalse();
        assertThat(dto.pseudo()).isEqualTo("alice");
    }
}
