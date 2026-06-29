package com.littlefish.app.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class DailyChallengeUser {

    @EmbeddedId
    private DailyChallengeUserId id = new DailyChallengeUserId();

    @ManyToOne
    @MapsId("dailyChallengeId")
    @JoinColumn(name = "daily_challenge_id")
    @JsonBackReference
    @ToString.Exclude
    private DailyChallenge dailyChallenge;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    private boolean completed;
    private LocalDate date;
}