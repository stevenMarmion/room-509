package com.littlefish.app.service;

import com.littlefish.app.model.Trade;
import com.littlefish.app.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public List<Trade> findAll() {
        return tradeRepository.findAll();
    }

    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }

    public Trade save(Trade trade) {
        return tradeRepository.save(trade);
    }

    public void deleteById(Long id) {
        tradeRepository.deleteById(id);
    }
}
