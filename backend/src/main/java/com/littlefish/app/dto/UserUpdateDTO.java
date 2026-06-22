package com.littlefish.app.dto;

import com.littlefish.app.model.enums.Theme;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private String pseudo;
    private String email;
    private String avatar;
    private Theme  theme;
}