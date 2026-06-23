package com.littlefish.app.dto;

import java.time.LocalDateTime;

public record FishUpdateDTO(
    String name,
    String species,
    String color,
    int price,
    int size,
    int age,
    int lifePoints,
    LocalDateTime lastFedAt,
    Long aquariumId
) {}