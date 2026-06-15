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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        aquariumService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
