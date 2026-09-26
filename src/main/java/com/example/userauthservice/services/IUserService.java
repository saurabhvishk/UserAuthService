package com.example.userauthservice.services;

import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.UserNotFoundException;
import com.example.userauthservice.models.User;

public interface IUserService {
    User getProfile(Long userId) throws UserNotFoundException;
    User updateProfile(Long userId, String name, String phone) throws UserNotFoundException;
    void changePassword(Long userId, String currentToken, String oldPassword, String newPassword)
            throws UserNotFoundException, InvalidCredentialsException;
}
