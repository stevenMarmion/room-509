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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tradeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
