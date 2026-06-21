package com.littlefish.app.dto;

import com.littlefish.app.model.Fish;
import com.littlefish.app.model.enums.TradeStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TradeDTO(
    Long id,
    TradeStatus status,
    LocalDateTime createdAt,
    int price,
    String initiatorPseudo,
    String receiverPseudo,
    List<Fish> fish
) {}