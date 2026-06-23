package com.littlefish.app.service;

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
    public Optional<NotificationPreference> update(Long id, NotificationPreference patch) {
        NotificationPreference existing = notificationPreferenceRepository.findById(id).orElse(null);
        if (existing == null) return Optional.empty();

        if (patch.isNotifyOnDeath() != existing.isNotifyOnDeath()) existing.setNotifyOnDeath(patch.isNotifyOnDeath());
        if (patch.isNotifyBeforeDeath() != existing.isNotifyBeforeDeath()) existing.setNotifyBeforeDeath(patch.isNotifyBeforeDeath());
        if (patch.isDailyReminder() != existing.isDailyReminder()) existing.setDailyReminder(patch.isDailyReminder());

        return Optional.of(notificationPreferenceRepository.save(existing));
    }

    @Transactional
    public void deleteById(Long id) {
        notificationPreferenceRepository.deleteById(id);
    }
}
