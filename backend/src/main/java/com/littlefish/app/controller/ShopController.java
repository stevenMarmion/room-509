package com.littlefish.app.controller;

import com.littlefish.app.model.AquariumUpgrade;
import com.littlefish.app.model.Fish;
import com.littlefish.app.model.Food;
import com.littlefish.app.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/fish")
    public List<Fish> getAllFish() {
        return shopService.findAllFish();
    }

    @PostMapping("/food/{id}/buy")
    public ResponseEntity<?> buyFood(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return shopService.buyFood(id, body.get("userId"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/upgrades/{id}/buy")
    public ResponseEntity<?> buyUpgrade(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return shopService.buyUpgrade(id, body.get("userId"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/fish/{id}/buy")
    public ResponseEntity<?> buyFish(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String pseudo = body.get("userId");
        return shopService.buyFish(id, pseudo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}
