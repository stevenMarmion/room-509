package com.littlefish.app.dto;

import com.littlefish.app.model.enums.Theme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserUpdateDTO")
class UserUpdateDTOTest {

    @Test @DisplayName("all fields accessible")
    void accessors() {
        UserUpdateDTO dto = new UserUpdateDTO("alice2", "new@test.com", "avatar.png", Theme.DARK);
        assertThat(dto.pseudo()).isEqualTo("alice2");
        assertThat(dto.email()).isEqualTo("new@test.com");
        assertThat(dto.avatar()).isEqualTo("avatar.png");
        assertThat(dto.theme()).isEqualTo(Theme.DARK);
    }

    @Test @DisplayName("partial update — only theme, other fields null")
    void partialUpdate() {
        UserUpdateDTO dto = new UserUpdateDTO(null, null, null, Theme.DARK);
        assertThat(dto.pseudo()).isNull();
        assertThat(dto.email()).isNull();
        assertThat(dto.avatar()).isNull();
        assertThat(dto.theme()).isEqualTo(Theme.DARK);
    }

    @Test @DisplayName("two instances with same values are equal")
    void equalsAndHashCode() {
        UserUpdateDTO a = new UserUpdateDTO("alice", "a@a.com", "img.png", Theme.LIGHT);
        UserUpdateDTO b = new UserUpdateDTO("alice", "a@a.com", "img.png", Theme.LIGHT);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
