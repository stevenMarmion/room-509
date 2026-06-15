package com.littlefish.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class DailyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int reward;
    private LocalDate date;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
