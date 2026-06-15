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

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> update(Long id, User patch) {
    return userRepository.findById(id).map(existing -> {
        log.info("Updating user with ID {}: {}, existing {}", id, patch, existing);
        if (patch.getPseudo()   != null) existing.setPseudo(patch.getPseudo());
        if (patch.getEmail()    != null) existing.setEmail(patch.getEmail());
        if (patch.getPassword() != null) existing.setPassword(patch.getPassword());
        if (patch.getTheme()    != null) existing.setTheme(patch.getTheme());
        if (patch.getAvatar()   != null) existing.setAvatar(patch.getAvatar());
        return userRepository.save(existing);
    });
}

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
