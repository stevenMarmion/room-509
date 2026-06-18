package com.littlefish.app.dto;

import com.littlefish.app.model.enums.FriendStatus;
import java.time.LocalDateTime;

public record FriendshipDTO (
    FriendStatus status,
    LocalDateTime since,
    String pseudo,
    LocalDateTime friendSince,
    String role
) {}