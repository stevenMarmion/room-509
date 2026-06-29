package com.littlefish.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FishCreateDTO")
class FishCreateDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        FishCreateDTO dto = new FishCreateDTO("Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 1L);
        assertThat(dto.name()).isEqualTo("Nemo");
        assertThat(dto.species()).isEqualTo("Clownfish");
        assertThat(dto.color()).isEqualTo("Orange");
        assertThat(dto.price()).isEqualTo(50);
        assertThat(dto.size()).isEqualTo(2);
        assertThat(dto.age()).isZero();
        assertThat(dto.lifePoints()).isEqualTo(100);
        assertThat(dto.aquariumId()).isEqualTo(1L);
    }

    @Test @DisplayName("two instances with same values are equal")
    void equalsAndHashCode() {
        FishCreateDTO a = new FishCreateDTO("Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 1L);
        FishCreateDTO b = new FishCreateDTO("Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 1L);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test @DisplayName("different aquariumId produces inequality")
    void notEqual() {
        FishCreateDTO a = new FishCreateDTO("Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 1L);
        FishCreateDTO b = new FishCreateDTO("Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 2L);
        assertThat(a).isNotEqualTo(b);
    }
}
