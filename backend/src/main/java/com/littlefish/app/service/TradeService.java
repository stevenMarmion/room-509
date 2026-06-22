package com.littlefish.app.service;

import com.littlefish.app.dto.CreateTradeRequest;
import com.littlefish.app.dto.TradeDTO;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Fish;
import com.littlefish.app.model.Trade;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.TradeStatus;
import com.littlefish.app.repository.FishRepository;
import com.littlefish.app.repository.TradeRepository;
import com.littlefish.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final FishRepository fishRepository;
    private final UserService userService;
    private final FishService fishService;

    public List<Trade> findAll() {
        return tradeRepository.findAll();
    }

    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }

    public TradeDTO createTrade(CreateTradeRequest request) {
        Trade trade = new Trade();

        trade.setInitiator(
            userService.findByPseudo(request.initiatorPseudo()).orElse(null)
        );
        trade.setReceiver(
            userService.findById(request.receiverId()).orElse(null)
        );
        trade.setPrice(request.price());
        trade.setStatus(TradeStatus.PENDING);
        trade.setCreatedAt(LocalDateTime.now());

        List<Fish> fishes = request.fishIds().stream()
            .map(id -> fishService.findById(id).orElse(null))
            .filter(f -> f != null)
            .toList();
        trade.setFish(fishes);

        Trade saved = tradeRepository.save(trade);
        return toDTO(saved);
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
        if (existingTrade == null) return Optional.empty();

        User initiator = userService.findByPseudo(existingTrade.getInitiator().getPseudo()).orElse(null);
        User receiver  = userService.findByPseudo(existingTrade.getReceiver().getPseudo()).orElse(null);

        if (initiator == null || receiver == null) return Optional.empty();

        List<Fish> tradedFish = existingTrade.getFish();

        initiator.setCoins(initiator.getCoins() + existingTrade.getPrice());
        receiver.setCoins(receiver.getCoins() - existingTrade.getPrice());

        Aquarium receiverAquarium = receiver.getAquarium();
        for (Fish fish : tradedFish) {
            fish.setAquarium(receiverAquarium);
            fishRepository.save(fish);
        }

        initiator.getAquarium().getFish().removeAll(tradedFish);
        receiverAquarium.getFish().addAll(tradedFish);

        userRepository.save(initiator);
        userRepository.save(receiver);

        existingTrade.setStatus(TradeStatus.ACCEPTED);
        return Optional.of(tradeRepository.save(existingTrade));
    }

    public Optional<Trade> rejectTrade(Long id) {
        return tradeRepository.findById(id).map(trade -> {
            trade.setStatus(TradeStatus.REJECTED);
            return tradeRepository.save(trade);
        });
    }
    
    public TradeDTO toDTO(Trade trade) {
        return new TradeDTO(
            trade.getId(),
            trade.getStatus(),
            trade.getCreatedAt(),
            trade.getPrice(),
            trade.getInitiator().getPseudo(),
            trade.getReceiver().getPseudo(),
            trade.getFish()
        );
    }

    public List<TradeDTO> findByPseudo(String pseudo) {
        return tradeRepository.findByInitiator_PseudoOrReceiver_Pseudo(pseudo, pseudo)
            .stream()
            .map(this::toDTO)
            .toList();
    }
}
