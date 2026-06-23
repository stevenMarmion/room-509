package com.littlefish.app.controller;

import com.littlefish.app.dto.UpdateCoinsRequest;
import com.littlefish.app.dto.UpdateRoleRequest;
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

    @DeleteMapping("/{pseudo}")
    public ResponseEntity<Void> delete(@PathVariable String pseudo) {
        userService.deleteByPseudo(pseudo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{pseudo}/role")
    public ResponseEntity<User> updateRole(@PathVariable String pseudo, @RequestBody UpdateRoleRequest request) {
        return userService.updateRole(pseudo, request.role())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{pseudo}/coins")
    public ResponseEntity<User> updateCoins(@PathVariable String pseudo, @RequestBody UpdateCoinsRequest request) {
        return userService.updateCoinsAdmin(pseudo, request.coins())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
