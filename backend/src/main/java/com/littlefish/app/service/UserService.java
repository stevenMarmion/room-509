package com.littlefish.app.service;

import com.littlefish.app.dto.FriendshipDTO;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Friendship;
import com.littlefish.app.model.NotificationPreference;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DailyChallengeService dailyChallengeService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByPseudo(String pseudo) {
        return userRepository.findByPseudo(pseudo);
    }

    public User registerUser(User user) {
        // Check if the pseudo or email already exists
        if (userRepository.findByPseudo(user.getPseudo()).isPresent()) {
            return null;
            
        } if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return null;
        }

        user.setAvatar("");
        user.setCoins(200);
        user.setRole(Role.USER);
        user.setTheme(Theme.LIGHT);
        user.setCreatedAt(LocalDateTime.now());

        Aquarium aquarium = new Aquarium();
        aquarium.setName(user.getPseudo() + "'s Aquarium");
        aquarium.setPublic(false);
        aquarium.setLevel(1);
        aquarium.setCapacity(10);
        aquarium.setUser(user);
        aquarium.setFish(List.of());
        user.setAquarium(aquarium);

        NotificationPreference notificationPreference = new NotificationPreference();
        notificationPreference.setNotifyOnDeath(true);
        notificationPreference.setNotifyBeforeDeath(true);
        notificationPreference.setDailyReminder(true);
        notificationPreference.setUser(user);
        user.setNotificationPreference(notificationPreference);

        user.setFriendships(List.of());
        user.setTrades(List.of());
        user.setDailyChallenges(dailyChallengeService.findRandom(3));

        return userRepository.save(user);
    }

    public Optional<User> update(String pseudo, User patch) {
        User existingUser = userRepository.findByPseudo(pseudo).orElse(null);
        if (patch.getPseudo() != null) {
            existingUser.setPseudo(patch.getPseudo());
        }
        if (patch.getEmail() != null) {
            existingUser.setEmail(patch.getEmail());
        }
        if (patch.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(patch.getPassword()));
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

    public void deleteByPseudo(String pseudo) {
        userRepository.deleteByPseudo(pseudo);
    }

    public FriendshipDTO toDTO(Friendship f, String requesterPseudo) {
        User friend = f.getRequester().getPseudo().equals(requesterPseudo) ? f.getAddressee(): f.getRequester();

        return new FriendshipDTO(
            f.getStatus(),
            f.getSince(),
            friend.getPseudo(),
            f.getSince(),
            friend.getRole().name()
        );
    }
}
