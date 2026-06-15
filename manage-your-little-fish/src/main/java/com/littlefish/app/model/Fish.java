package com.littlefish.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Fish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private String color;
    private int size;
    private int age;
    private int lifePoints;
    private LocalDateTime lastFedAt;

    @ManyToOne
    @JoinColumn(name = "aquarium_id")
    private Aquarium aquarium;

    @ManyToMany(mappedBy = "fish")
    private List<Trade> trades;
}
