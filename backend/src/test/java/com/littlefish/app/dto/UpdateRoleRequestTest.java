package com.littlefish.app.dto;

import com.littlefish.app.model.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UpdateRoleRequest")
class UpdateRoleRequestTest {

    @Test @DisplayName("role field is accessible")
    void accessor() {
        UpdateRoleRequest req = new UpdateRoleRequest(Role.ADMIN);
        assertThat(req.role()).isEqualTo(Role.ADMIN);
    }

    @Test @DisplayName("USER role is also valid")
    void userRole() {
        UpdateRoleRequest req = new UpdateRoleRequest(Role.USER);
        assertThat(req.role()).isEqualTo(Role.USER);
    }

    @Test @DisplayName("two instances with same role are equal")
    void equalsAndHashCode() {
        UpdateRoleRequest a = new UpdateRoleRequest(Role.ADMIN);
        UpdateRoleRequest b = new UpdateRoleRequest(Role.ADMIN);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test @DisplayName("different roles produce inequality")
    void notEqual() {
        UpdateRoleRequest a = new UpdateRoleRequest(Role.USER);
        UpdateRoleRequest b = new UpdateRoleRequest(Role.ADMIN);
        assertThat(a).isNotEqualTo(b);
    }
}
