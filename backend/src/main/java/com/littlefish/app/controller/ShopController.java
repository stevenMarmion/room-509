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

    @PostMapping("/food")
    public Food createFood(@RequestBody Food food) {
        return shopService.createFood(food);
    }

    @PutMapping("/food/{id}")
    public ResponseEntity<Food> updateFood(@PathVariable Long id, @RequestBody Food patch) {
        return shopService.updateFood(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/food/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        shopService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upgrades")
    public AquariumUpgrade createUpgrade(@RequestBody AquariumUpgrade upgrade) {
        return shopService.createUpgrade(upgrade);
    }

    @PutMapping("/upgrades/{id}")
    public ResponseEntity<AquariumUpgrade> updateUpgrade(@PathVariable Long id, @RequestBody AquariumUpgrade patch) {
        return shopService.updateUpgrade(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/upgrades/{id}")
    public ResponseEntity<Void> deleteUpgrade(@PathVariable Long id) {
        shopService.deleteUpgrade(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/fish")
    public Fish createShopFish(@RequestBody Fish fish) {
        return shopService.createShopFish(fish);
    }

    @PutMapping("/fish/{id}")
    public ResponseEntity<Fish> updateShopFish(@PathVariable Long id, @RequestBody Fish patch) {
        return shopService.updateShopFish(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/fish/{id}")
    public ResponseEntity<Void> deleteShopFish(@PathVariable Long id) {
        shopService.deleteShopFish(id);
        return ResponseEntity.noContent().build();
    }
}
