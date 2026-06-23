package com.littlefish.app.controller;

import com.littlefish.app.model.NotificationPreference;
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
    public List<NotificationPreference> getAll() {
        return notificationPreferenceService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationPreference> getById(@PathVariable Long id) {
        return notificationPreferenceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationPreference> update(@PathVariable Long id, @RequestBody NotificationPreference patch) {
        return notificationPreferenceService.update(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationPreferenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
