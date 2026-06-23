package com.littlefish.app.service;

import com.littlefish.app.dto.NotificationPreferenceUpdateDTO;
import com.littlefish.app.model.NotificationPreference;
import com.littlefish.app.repository.NotificationPreferenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public List<NotificationPreference> findAll() {
        return notificationPreferenceRepository.findAll();
    }

    public Optional<NotificationPreference> findById(Long id) {
        return notificationPreferenceRepository.findById(id);
    }

    @Transactional
    public Optional<NotificationPreference> update(Long id, NotificationPreferenceUpdateDTO patch) {
        Optional<NotificationPreference> existingPreference = notificationPreferenceRepository.findAll().stream()
                .filter(pref -> pref.getUser().getPseudo().equals(patch.pseudo()))
                .findFirst();
        if (existingPreference.isPresent()) {
            NotificationPreference pref = existingPreference.get();
            if (patch.notifyOnDeath() != null) pref.setNotifyOnDeath(patch.notifyOnDeath());
            if (patch.notifyBeforeDeath() != null) pref.setNotifyBeforeDeath(patch.notifyBeforeDeath());
            if (patch.dailyReminder() != null) pref.setDailyReminder(patch.dailyReminder());
            return Optional.of(notificationPreferenceRepository.save(pref));
        }
        return Optional.empty();
    }

    @Transactional
    public void deleteById(Long id) {
        notificationPreferenceRepository.deleteById(id);
    }
}
