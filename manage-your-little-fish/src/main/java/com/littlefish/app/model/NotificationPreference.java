package com.littlefish.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean notifyOnDeath;
    private boolean notifyBeforeDeath;
    private boolean dailyReminder;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
