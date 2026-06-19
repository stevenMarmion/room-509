package com.littlefish.app.dto;

public record LoginRequest(
    String pseudo, 
    String password
) {}