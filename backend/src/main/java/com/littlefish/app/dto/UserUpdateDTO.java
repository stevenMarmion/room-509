package com.littlefish.app.dto;

import com.littlefish.app.model.enums.Theme;

public record UserUpdateDTO(
    String pseudo,
    String email,
    String avatar,
    Theme  theme
) {}