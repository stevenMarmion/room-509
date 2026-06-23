package com.littlefish.app.controller;

import com.littlefish.app.dto.UserUpdateDTO;
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

    @GetMapping("/{pseudo}")
    public ResponseEntity<User> getByPseudo(@PathVariable String pseudo) {
        return userService.findByPseudo(pseudo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{pseudo}/fishes")
    public ResponseEntity<?> getFishesByPseudo(@PathVariable String pseudo) {
        return userService.findByPseudo(pseudo)
            .map(user -> ResponseEntity.ok().body(user.getAquarium().getFish()))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{pseudo}")
    public ResponseEntity<User> update(@PathVariable String pseudo, @RequestBody UserUpdateDTO dto) {
        return userService.update(pseudo, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{pseudo}/earn")
    public ResponseEntity<User> earnCoins(@PathVariable String pseudo) {
        return userService.earnCoins(pseudo, 1)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{pseudo}")
    public ResponseEntity<Void> delete(@PathVariable String pseudo) {
        userService.deleteByPseudo(pseudo);
        return ResponseEntity.noContent().build();
    }
}
