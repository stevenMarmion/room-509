package com.littlefish.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
public class DailyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int reward;
    private String description;

    @OneToMany(mappedBy = "dailyChallenge", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DailyChallengeUser> userEntries;
}