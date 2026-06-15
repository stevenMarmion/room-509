package com.littlefish.app.controller;

import com.littlefish.app.model.Fish;
import com.littlefish.app.service.FishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fish")
@RequiredArgsConstructor
public class FishController {

    private final FishService fishService;

    @GetMapping
    public List<Fish> getAll() {
        return fishService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fish> getById(@PathVariable Long id) {
        return fishService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Fish create(@RequestBody Fish fish) {
        return fishService.save(fish);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fishService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
