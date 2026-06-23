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
    private static final int RANDOM_CHALLENGE_COUNT = 3;

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

    @GetMapping("/random")
    public ResponseEntity<List<DailyChallenge>> getRandom() {
        return ResponseEntity.ok().body(dailyChallengeService.findRandom(RANDOM_CHALLENGE_COUNT));
    }

    @PostMapping
    public DailyChallenge create(@RequestBody DailyChallenge challenge) {
        return dailyChallengeService.save(challenge);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyChallenge> update(@PathVariable Long id, @RequestBody DailyChallenge patch) {
        return dailyChallengeService.update(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dailyChallengeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
