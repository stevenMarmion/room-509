package com.littlefish.app.controller;

import com.littlefish.app.dto.NotificationPreferenceRequestDTO;
import com.littlefish.app.dto.NotificationPreferenceUpdateDTO;
import com.littlefish.app.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;

    @GetMapping
    public List<NotificationPreferenceRequestDTO> getAll() {
        return notificationPreferenceService.findAll().stream()
                .map(pref -> new NotificationPreferenceRequestDTO(
                        pref.getId(),
                        pref.isNotifyOnDeath(),
                        pref.isNotifyBeforeDeath(),
                        pref.isDailyReminder(),
                        pref.getUser().getPseudo()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationPreferenceRequestDTO> getById(@PathVariable Long id) {
        return notificationPreferenceService.findById(id)
                .map(pref -> new NotificationPreferenceRequestDTO(
                        pref.getId(),
                        pref.isNotifyOnDeath(),
                        pref.isNotifyBeforeDeath(),
                        pref.isDailyReminder(),
                        pref.getUser().getPseudo()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationPreferenceRequestDTO> update(@PathVariable Long id, @RequestBody NotificationPreferenceUpdateDTO updateDTO) {
        return notificationPreferenceService.update(id, updateDTO)
                .map(pref -> new NotificationPreferenceRequestDTO(
                        pref.getId(),
                        pref.isNotifyOnDeath(),
                        pref.isNotifyBeforeDeath(),
                        pref.isDailyReminder(),
                        pref.getUser().getPseudo()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationPreferenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
