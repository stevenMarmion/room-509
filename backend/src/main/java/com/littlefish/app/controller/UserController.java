package com.littlefish.app.controller;

import com.littlefish.app.dto.FriendshipDTO;
import com.littlefish.app.model.User;
import com.littlefish.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pseudo/{pseudo}")
    public ResponseEntity<User> getByPseudo(@PathVariable String pseudo) {
        return userService.findByPseudo(pseudo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pseudo/{pseudo}/fishes")
    public ResponseEntity<?> getFishesByPseudo(@PathVariable String pseudo) {
        return userService.findByPseudo(pseudo)
            .map(user -> ResponseEntity.ok().body(user.getAquarium().getFish()))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pseudo/{pseudo}/friends")
    public ResponseEntity<?> getFriendsByPseudo(@PathVariable String pseudo) {
        return userService.findByPseudo(pseudo)
            .map(user -> {
                List<FriendshipDTO> dtos = user.getFriendships()
                    .stream()
                    .map(f -> userService.toDTO(f, pseudo))
                    .toList();
                return ResponseEntity.ok().body(dtos);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User patch) {
        return userService.update(id, patch)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
