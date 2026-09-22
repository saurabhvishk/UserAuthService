package com.example.userauthservice.services;

import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.UserAlreadyExixtsException;
import com.example.userauthservice.models.User;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.util.MultiValueMap;

public interface IAuthService {
    User signup(String email, String password) throws UserAlreadyExixtsException;
    Pair<User, MultiValueMap<String, String>> login(String email, String password) throws InvalidCredentialsException;
    User logout(String email);
}
