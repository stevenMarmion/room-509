package com.littlefish.app.dto;

import java.time.LocalDateTime;

public record FriendshipRequestDTO(
    Long id,
    String status,
    LocalDateTime since,
    String senderUsername,
    String receiverUsername
) {}
