package com.littlefish.app.controller;

import com.littlefish.app.model.DailyChallenge;
import com.littlefish.app.service.DailyChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class DailyChallengeController {

    private final DailyChallengeService dailyChallengeService;

    @GetMapping
    public List<DailyChallenge> getAll() {
        return dailyChallengeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyChallenge> getById(@PathVariable Long id) {
        return dailyChallengeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DailyChallenge create(@RequestBody DailyChallenge dailyChallenge) {
        return dailyChallengeService.save(dailyChallenge);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dailyChallengeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
