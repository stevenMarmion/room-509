package com.littlefish.app.service;

import com.littlefish.app.model.Trade;
import com.littlefish.app.model.enums.TradeStatus;
import com.littlefish.app.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserService userService;

    public List<Trade> findAll() {
        return tradeRepository.findAll();
    }

    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }

    public Trade save(Trade trade) {
        trade.setStatus(TradeStatus.PENDING);
        trade.setCreatedAt(LocalDateTime.now());
        return tradeRepository.save(trade);
    }

    public Optional<Trade> update(Long id, Trade patch) {
        Trade existingTrade = tradeRepository.findById(id).orElse(null);
        if (existingTrade != null) {
            if (patch.getFish() != null) {
                existingTrade.setFish(patch.getFish());
            }
            if (patch.getPrice() != 0) {
                existingTrade.setPrice(patch.getPrice());
            }
            return Optional.of(tradeRepository.save(existingTrade));
        }
        return Optional.empty();
    }

    public void deleteById(Long id) {
        tradeRepository.deleteById(id);
    }

    public Optional<Trade> acceptTrade(Long id) {
        Trade existingTrade = tradeRepository.findById(id).orElse(null);
        if (existingTrade != null) {
            userService.findByPseudo(existingTrade.getInitiator().getPseudo()).ifPresent(user -> {
                user.setCoins(user.getCoins() + existingTrade.getPrice());
                user.getAquarium().getFish().removeAll(existingTrade.getFish());
                userService.update(user.getPseudo(), user);
            });
            userService.findByPseudo(existingTrade.getReceiver().getPseudo()).ifPresent(user -> {
                user.setCoins(user.getCoins() - existingTrade.getPrice());
                user.getAquarium().getFish().addAll(existingTrade.getFish());
                userService.update(user.getPseudo(), user);
            });
            existingTrade.setStatus(TradeStatus.ACCEPTED);
            return Optional.of(tradeRepository.save(existingTrade));
        }
        return Optional.empty();
    }

    public Optional<Trade> rejectTrade(Long id) {
        Trade existingTrade = tradeRepository.findById(id).orElse(null);
        if (existingTrade != null) {
            existingTrade.setStatus(TradeStatus.REJECTED);
            return Optional.of(tradeRepository.save(existingTrade));
        }
        return Optional.empty();
    }
}
