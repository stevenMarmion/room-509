package com.littlefish.app.service;

import com.littlefish.app.model.User;
import com.littlefish.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> update(Long id, User patch) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (patch.getPseudo() != null) {
            existingUser.setPseudo(patch.getPseudo());
        }
        if (patch.getEmail() != null) {
            existingUser.setEmail(patch.getEmail());
        }
        if (patch.getPassword() != null) {
            existingUser.setPassword(patch.getPassword());
        }
        if (patch.getTheme() != null) {
            existingUser.setTheme(patch.getTheme());
        }
        if (patch.getAvatar() != null) {
            existingUser.setAvatar(patch.getAvatar());
        }
        if (patch.getCoins() != 0) {
            existingUser.setCoins(patch.getCoins());
        }
        return Optional.of(userRepository.save(existingUser));
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
