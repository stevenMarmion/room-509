package com.littlefish.app.model;

import com.littlefish.app.model.enums.TradeStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToMany
    @JoinTable(
        name = "trade_fish",
        joinColumns = @JoinColumn(name = "trade_id"),
        inverseJoinColumns = @JoinColumn(name = "fish_id")
    )
    private List<Fish> fish;
}
