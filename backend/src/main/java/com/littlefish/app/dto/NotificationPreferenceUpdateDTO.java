package com.littlefish.app.dto;

public record NotificationPreferenceUpdateDTO (
    Boolean notifyOnDeath,
    Boolean notifyBeforeDeath,
    Boolean dailyReminder,
    String pseudo
) {}
