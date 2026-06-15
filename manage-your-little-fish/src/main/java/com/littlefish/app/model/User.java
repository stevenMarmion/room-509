package com.littlefish.app.model;

import com.littlefish.app.model.enums.Role;
import com.littlefish.app.model.enums.Theme;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pseudo;
    private String email;
    private String password;
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    private int coins;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Aquarium aquarium;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NotificationPreference notificationPreference;

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL)
    private List<Friendship> friendships;

    @OneToMany(mappedBy = "initiator", cascade = CascadeType.ALL)
    private List<Trade> trades;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<DailyChallenge> dailyChallenges;
}
