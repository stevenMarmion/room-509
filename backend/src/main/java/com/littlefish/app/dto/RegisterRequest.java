package com.littlefish.app.dto;

public record RegisterRequest(
    String pseudo, 
    String email, 
    String password, 
    String passwordConfirm
) {}