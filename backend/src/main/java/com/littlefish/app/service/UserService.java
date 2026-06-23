package com.littlefish.app.service;

import com.littlefish.app.dto.UserUpdateDTO;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Fish;
import com.littlefish.app.model.NotificationPreference;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import com.littlefish.app.repository.FishRepository;
import com.littlefish.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FishRepository fishRepository;
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

        List<Fish> availableFishes = fishRepository.findAll();
        Fish selectedFish = availableFishes.get((int) (Math.random() * availableFishes.size()));
        Fish fishCopy = new Fish();
        fishCopy.setName(selectedFish.getName());
        fishCopy.setSpecies(selectedFish.getSpecies());
        fishCopy.setColor(selectedFish.getColor());
        fishCopy.setSize(selectedFish.getSize());
        fishCopy.setAge(selectedFish.getAge());
        fishCopy.setLifePoints(selectedFish.getLifePoints());
        fishCopy.setLastFedAt(LocalDateTime.now());

        Aquarium aquarium = new Aquarium();
        aquarium.setName("Aquarium");
        aquarium.setPublic(false);
        aquarium.setLevel(1);
        aquarium.setCapacity(10);
        aquarium.setUser(user);
        aquarium.setFish(List.of(fishCopy));

        fishCopy.setAquarium(aquarium);
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

    public Optional<User> update(String pseudo, UserUpdateDTO dto) {
        User user = userRepository.findByPseudo(pseudo).orElse(null);
        if (user == null) return Optional.empty();

        if (dto.getPseudo()  != null) user.setPseudo(dto.getPseudo());
        if (dto.getEmail()   != null) user.setEmail(dto.getEmail());
        if (dto.getAvatar()  != null) user.setAvatar(dto.getAvatar());
        if (dto.getTheme()   != null) user.setTheme(dto.getTheme());

        return Optional.of(userRepository.save(user));
    }

    public void deleteByPseudo(String pseudo) {
        userRepository.deleteByPseudo(pseudo);
    }

    @Transactional
    public Optional<User> updateRole(String pseudo, Role role) {
        User user = userRepository.findByPseudo(pseudo).orElse(null);
        if (user == null) return Optional.empty();

        user.setRole(role);
        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public Optional<User> updateCoinsAdmin(String pseudo, int coins) {
        if (userRepository.findByPseudo(pseudo).isEmpty()) return Optional.empty();

        userRepository.updateCoins(pseudo, coins);
        return userRepository.findByPseudo(pseudo);
    }
}
