package com.littlefish.app.repository;

import com.littlefish.app.model.Trade;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    public List<Trade> findByInitiator_PseudoOrReceiver_Pseudo(String initiatorPseudo, String receiverPseudo);
}
