package com.littlefish.app.controller;

import com.littlefish.app.model.Trade;
import com.littlefish.app.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public List<Trade> getAll() {
        return tradeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trade> getById(@PathVariable Long id) {
        return tradeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Trade create(@RequestBody Trade trade) {
        return tradeService.save(trade);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trade> update(@PathVariable Long id, @RequestBody Trade patch) {
        return tradeService.update(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tradeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Trade> acceptTrade(@PathVariable Long id) {
        return tradeService.acceptTrade(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Trade> rejectTrade(@PathVariable Long id) {
        return tradeService.rejectTrade(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
