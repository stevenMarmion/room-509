package com.littlefish.app.dto;

public record FishCreateDTO(
    String name,
    String species,
    String color,
    int price,
    int size,
    int age,
    int lifePoints,
    Long aquariumId
) {}
