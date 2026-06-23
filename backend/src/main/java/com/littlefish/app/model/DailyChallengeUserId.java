package com.littlefish.app.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class DailyChallengeUserId implements Serializable {
    private Long dailyChallengeId;
    private Long userId;
}