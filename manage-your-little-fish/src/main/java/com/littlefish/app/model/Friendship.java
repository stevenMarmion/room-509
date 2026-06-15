package com.littlefish.app.model;

import com.littlefish.app.model.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    private LocalDateTime since;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "addressee_id")
    private User addressee;
}
