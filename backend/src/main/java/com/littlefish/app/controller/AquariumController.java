package com.littlefish.app.controller;

import com.littlefish.app.model.Aquarium;
import com.littlefish.app.service.AquariumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aquariums")
@RequiredArgsConstructor
public class AquariumController {

    private final AquariumService aquariumService;

    @GetMapping
    public List<Aquarium> getAll() {
        return aquariumService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aquarium> getById(@PathVariable Long id) {
        return aquariumService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Aquarium create(@RequestBody Aquarium aquarium) {
        return aquariumService.save(aquarium);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aquarium> update(@PathVariable Long id, @RequestBody Aquarium patch) {
        return aquariumService.update(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<Aquarium> updateVisibility(@PathVariable Long id, @RequestBody boolean isPublic) {
        return aquariumService.switchVisibility(id, isPublic)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        aquariumService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
