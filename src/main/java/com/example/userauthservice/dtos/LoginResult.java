package com.example.userauthservice.dtos;

import com.example.userauthservice.models.User;

public record LoginResult(User user, String token) {}
