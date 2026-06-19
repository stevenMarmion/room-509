package com.littlefish.app.controller;

import com.littlefish.app.dto.LoginRequest;
import com.littlefish.app.dto.RegisterRequest;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.User;
import com.littlefish.app.service.JwtService;
import com.littlefish.app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest req, HttpServletResponse response) {
        if (!req.password().equals(req.passwordConfirm())) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setPseudo(req.pseudo());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));

        User saved = userService.registerUser(user);
        if (saved == null) {
            return ResponseEntity.status(400).build();
        }

        setAuthCookie(response, jwtService.generateToken(saved.getPseudo()));
        return ResponseEntity.status(201).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        return userService.findByPseudo(req.pseudo())
            .filter(user -> passwordEncoder.matches(req.password(), user.getPassword()))
            .map(user -> {
                setAuthCookie(response, jwtService.generateToken(user.getPseudo()));
                return ResponseEntity.ok(user);
            })
            .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 24h
        response.addCookie(cookie);
    }
}