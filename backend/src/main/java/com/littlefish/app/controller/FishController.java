package com.littlefish.app.controller;

import com.littlefish.app.model.Fish;
import com.littlefish.app.service.FishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fish")
@RequiredArgsConstructor
public class FishController {

    private final FishService fishService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(fishService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fish> getById(@PathVariable Long id) {
        return fishService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fish> updateName(@PathVariable Long id, @RequestBody String fishName) {
        return fishService.updateName(id, fishName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
