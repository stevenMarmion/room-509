package com.littlefish.app.controller;

import com.littlefish.app.model.AquariumUpgrade;
import com.littlefish.app.model.Food;
import com.littlefish.app.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/food")
    public List<Food> getAllFood() {
        return shopService.findAllFood();
    }

    @GetMapping("/food/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {
        return shopService.findFoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/upgrades")
    public List<AquariumUpgrade> getAllUpgrades() {
        return shopService.findAllUpgrades();
    }

    @GetMapping("/upgrades/{id}")
    public ResponseEntity<AquariumUpgrade> getUpgradeById(@PathVariable Long id) {
        return shopService.findUpgradeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
