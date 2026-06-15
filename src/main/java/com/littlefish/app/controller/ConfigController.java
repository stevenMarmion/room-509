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
}
