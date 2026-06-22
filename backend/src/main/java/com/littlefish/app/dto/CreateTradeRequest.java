package com.littlefish.app.dto;

import java.util.List;

public record CreateTradeRequest(
    String initiatorPseudo,
    Long receiverId,
    int price,
    List<Long> fishIds
) {}