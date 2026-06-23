package com.littlefish.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.littlefish.app.dto.FriendshipDTO;
import com.littlefish.app.dto.FriendshipRequestDTO;
import com.littlefish.app.model.Friendship;
import com.littlefish.app.service.FriendshipService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @GetMapping
    public ResponseEntity<List<FriendshipRequestDTO>> getAll() {
        List<Friendship> friendships = friendshipService.findAll();
        List<FriendshipRequestDTO> friendshipRequestDTOs = friendships.stream()
            .map(f -> new FriendshipRequestDTO(
                f.getId(),
                f.getStatus().name(),
                f.getSince(),
                f.getRequester().getPseudo(),
                f.getAddressee().getPseudo()
            ))
            .toList();
        return ResponseEntity.ok(friendshipRequestDTOs);
    }

    @GetMapping("/{pseudo}/friends")
    public ResponseEntity<List<FriendshipDTO>> getFriendsByPseudo(@PathVariable String pseudo) {
        return ResponseEntity.ok(friendshipService.findAcceptedFriends(pseudo));
    }

    @GetMapping("/{pseudo}/pending")
    public ResponseEntity<List<FriendshipDTO>> getPendingRequests(@PathVariable String pseudo) {
        return ResponseEntity.ok(friendshipService.findPendingRequestsReceivedBy(pseudo));
    }

    @PostMapping("/{pseudo}/add-friend")
    public ResponseEntity<?> addFriend(@PathVariable String pseudo, @RequestParam String friendPseudo) {
        return friendshipService.addFriend(pseudo, friendPseudo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{pseudo}/accept-friend")
    public ResponseEntity<?> acceptFriend(@PathVariable String pseudo, @RequestParam String friendPseudo) {
        return friendshipService.acceptFriend(pseudo, friendPseudo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{pseudo}/reject-friend")
    public ResponseEntity<?> rejectFriend(@PathVariable String pseudo, @RequestParam String friendPseudo) {
        return friendshipService.rejectFriend(pseudo, friendPseudo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        friendshipService.deleteFriendship(id);
        return ResponseEntity.noContent().build(); 
    }

}