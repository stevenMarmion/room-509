package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FishUpdateDTO")
class FishUpdateDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        LocalDateTime now = LocalDateTime.now();
        FishUpdateDTO dto = new FishUpdateDTO("Dory", "Tang", "Blue", 80, 3, 2, 75, now, 2L);
        assertThat(dto.name()).isEqualTo("Dory");
        assertThat(dto.species()).isEqualTo("Tang");
        assertThat(dto.color()).isEqualTo("Blue");
        assertThat(dto.price()).isEqualTo(80);
        assertThat(dto.size()).isEqualTo(3);
        assertThat(dto.age()).isEqualTo(2);
        assertThat(dto.lifePoints()).isEqualTo(75);
        assertThat(dto.lastFedAt()).isEqualTo(now);
        assertThat(dto.aquariumId()).isEqualTo(2L);
    }

    @Test @DisplayName("null fields for partial patch")
    void partialPatch() {
        FishUpdateDTO dto = new FishUpdateDTO("NewName", null, null, 0, 0, 0, 0, null, null);
        assertThat(dto.name()).isEqualTo("NewName");
        assertThat(dto.species()).isNull();
        assertThat(dto.aquariumId()).isNull();
    }
}
