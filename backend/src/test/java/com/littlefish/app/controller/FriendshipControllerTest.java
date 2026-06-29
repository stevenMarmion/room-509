package com.littlefish.app.controller;

import com.littlefish.app.config.SecurityConfigTest;
import com.littlefish.app.dto.FriendshipDTO;
import com.littlefish.app.model.Friendship;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.FriendStatus;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.service.FriendshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ============================================================
//  FriendshipControllerTest
// ============================================================
@WebMvcTest(FriendshipController.class)
@Import(SecurityConfigTest.class)
@DisplayName("FriendshipController")
class FriendshipControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean FriendshipService friendshipService;

    private User alice;
    private User bob;
    private Friendship friendship;
    private FriendshipDTO friendshipDTO;

    @BeforeEach
    void setUp() {
        alice = new User(); alice.setId(1L); alice.setPseudo("alice"); alice.setRole(Role.USER);
        bob   = new User(); bob.setId(2L);   bob.setPseudo("bob");     bob.setRole(Role.USER);

        friendship = new Friendship();
        friendship.setId(1L);
        friendship.setRequester(alice);
        friendship.setAddressee(bob);
        friendship.setStatus(FriendStatus.ACCEPTED);
        friendship.setSince(LocalDateTime.of(2026, 1, 1, 0, 0));

        friendshipDTO = new FriendshipDTO(
            FriendStatus.ACCEPTED,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            "bob",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            "USER"
        );
    }

    @Nested @DisplayName("GET /api/friendships")
    class GetAll {
        @Test @DisplayName("returns 200 with list of all friendships as DTO")
        void returnsList() throws Exception {
            when(friendshipService.findAll()).thenReturn(List.of(friendship));

            mockMvc.perform(get("/api/friendships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("ACCEPTED")))
                .andExpect(jsonPath("$[0].senderUsername", is("alice")))
                .andExpect(jsonPath("$[0].receiverUsername", is("bob")));
        }
    }

    @Nested @DisplayName("GET /api/friendships/{pseudo}/friends")
    class GetFriends {
        @Test @DisplayName("returns 200 with accepted friends")
        void returnsFriends() throws Exception {
            when(friendshipService.findAcceptedFriends("alice")).thenReturn(List.of(friendshipDTO));

            mockMvc.perform(get("/api/friendships/alice/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pseudo", is("bob")));
        }
    }

    @Nested @DisplayName("GET /api/friendships/{pseudo}/pending")
    class GetPending {
        @Test @DisplayName("returns 200 with pending requests")
        void returnsPending() throws Exception {
            FriendshipDTO pending = new FriendshipDTO(
                FriendStatus.PENDING,
                LocalDateTime.now(), "charlie", LocalDateTime.now(), "USER");
            when(friendshipService.findPendingRequestsReceivedBy("alice")).thenReturn(List.of(pending));

            mockMvc.perform(get("/api/friendships/alice/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("PENDING")));
        }
    }

    @Nested @DisplayName("POST /api/friendships/{pseudo}/add-friend")
    class AddFriend {
        @Test @DisplayName("returns 200 when request sent successfully")
        void success() throws Exception {
            when(friendshipService.addFriend("alice", "bob")).thenReturn(Optional.of(alice));

            mockMvc.perform(post("/api/friendships/alice/add-friend")
                    .param("friendPseudo", "bob"))
                .andExpect(status().isOk());
        }

        @Test @DisplayName("returns 404 when target user not found")
        void notFound() throws Exception {
            when(friendshipService.addFriend("alice", "ghost")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/friendships/alice/add-friend")
                    .param("friendPseudo", "ghost"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /api/friendships/{pseudo}/accept-friend")
    class AcceptFriend {
        @Test @DisplayName("returns 200 when friendship accepted")
        void success() throws Exception {
            when(friendshipService.acceptFriend("bob", "alice")).thenReturn(Optional.of(bob));

            mockMvc.perform(post("/api/friendships/bob/accept-friend")
                    .param("friendPseudo", "alice"))
                .andExpect(status().isOk());
        }
    }

    @Nested @DisplayName("POST /api/friendships/{pseudo}/reject-friend")
    class RejectFriend {
        @Test @DisplayName("returns 200 when friendship rejected")
        void success() throws Exception {
            when(friendshipService.rejectFriend("bob", "alice")).thenReturn(Optional.of(bob));

            mockMvc.perform(post("/api/friendships/bob/reject-friend")
                    .param("friendPseudo", "alice"))
                .andExpect(status().isOk());
        }
    }

    @Nested @DisplayName("DELETE /api/friendships/{id}")
    class Delete {
        @Test @DisplayName("returns 204 No Content")
        void deletes() throws Exception {
            doNothing().when(friendshipService).deleteFriendship(1L);

            mockMvc.perform(delete("/api/friendships/1"))
                .andExpect(status().isNoContent());

            verify(friendshipService).deleteFriendship(1L);
        }
    }
}
