package com.example.userauthservice.services;

import com.example.userauthservice.dtos.LoginResult;
import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.InvalidTokenException;
import com.example.userauthservice.exception.UserAlreadyExistsException;
import com.example.userauthservice.models.User;

public interface IAuthService {
    User signup(String email, String password) throws UserAlreadyExistsException;
    LoginResult login(String email, String password) throws InvalidCredentialsException;
    void logout(String token, Long userId) throws InvalidTokenException;
    Boolean validateToken(String token,Long userId);
}
