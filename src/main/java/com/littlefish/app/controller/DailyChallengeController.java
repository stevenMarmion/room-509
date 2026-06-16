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
}
