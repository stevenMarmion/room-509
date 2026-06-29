package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificationPreferenceUpdateDTO")
class NotificationPreferenceUpdateDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        NotificationPreferenceUpdateDTO dto =
            new NotificationPreferenceUpdateDTO(true, false, true, "alice");
        assertThat(dto.notifyOnDeath()).isTrue();
        assertThat(dto.notifyBeforeDeath()).isFalse();
        assertThat(dto.dailyReminder()).isTrue();
        assertThat(dto.pseudo()).isEqualTo("alice");
    }

    @Test @DisplayName("null fields accepted for partial update")
    void nullFieldsAccepted() {
        NotificationPreferenceUpdateDTO dto =
            new NotificationPreferenceUpdateDTO(null, null, true, "alice");
        assertThat(dto.notifyOnDeath()).isNull();
        assertThat(dto.notifyBeforeDeath()).isNull();
        assertThat(dto.dailyReminder()).isTrue();
    }

    @Test @DisplayName("two instances with same values are equal")
    void equalsAndHashCode() {
        NotificationPreferenceUpdateDTO a =
            new NotificationPreferenceUpdateDTO(true, false, true, "alice");
        NotificationPreferenceUpdateDTO b =
            new NotificationPreferenceUpdateDTO(true, false, true, "alice");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
