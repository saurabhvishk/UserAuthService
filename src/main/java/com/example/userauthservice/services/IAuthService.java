package com.example.userauthservice.services;

import com.example.userauthservice.exception.UserAlreadyExixtsException;
import com.example.userauthservice.models.User;

public interface IAuthService {
    User signup(String email, String password) throws UserAlreadyExixtsException;
    User login(String email, String password);
    User logout(String email);
}
