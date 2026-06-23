package com.littlefish.app.dto;

public record NotificationPreferenceRequestDTO (
    Long id,
    Boolean notifyOnDeath,
    Boolean notifyBeforeDeath,
    Boolean dailyReminder,
    String pseudo
) {}
