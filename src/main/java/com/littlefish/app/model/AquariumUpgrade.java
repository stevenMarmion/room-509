package com.littlefish.app.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AquariumUpgrade extends ShopItem {

    private int capacityBonus;
    private int levelBonus;
}
