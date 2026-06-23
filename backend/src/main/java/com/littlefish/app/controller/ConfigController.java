package com.littlefish.app.controller;

import com.littlefish.app.model.Config;
import com.littlefish.app.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    public List<Config> getAll() {
        return configService.findAll();
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getByKey(@PathVariable String key) {
        return configService.findByKey(key)
                .map(config -> ResponseEntity.ok().body(config))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Config create(@RequestBody Config config) {
        return configService.save(config);
    }

    @PutMapping("/{key}")
    public ResponseEntity<Config> update(@PathVariable String key, @RequestBody Config patch) {
        return configService.update(key, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        configService.deleteByKey(key);
        return ResponseEntity.noContent().build();
    }
}
