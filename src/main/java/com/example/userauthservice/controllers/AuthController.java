package com.example.userauthservice.controllers;

import com.example.userauthservice.dtos.LoginRequestDto;
import com.example.userauthservice.dtos.LogoutRequestDto;
import com.example.userauthservice.dtos.SignupRequestDto;
import com.example.userauthservice.dtos.UserDto;
import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.UserAlreadyExixtsException;
import com.example.userauthservice.models.User;
import com.example.userauthservice.services.IAuthService;
import jakarta.persistence.Id;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequestDto) throws UserAlreadyExixtsException {
        if(signupRequestDto.getEmail() == null || signupRequestDto.getPassword() == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }
        User user = authService.signup(signupRequestDto.getEmail(),signupRequestDto.getPassword());
        return new ResponseEntity<>(from(user), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) throws InvalidCredentialsException {
        Pair<User, MultiValueMap<String, String>> userWithToken = authService.login(loginRequestDto.getEmail(),loginRequestDto.getPassword());
        if(userWithToken.a == null){
            throw new NullPointerException("User not found");
        }
        return new ResponseEntity<>(from(userWithToken.a),userWithToken.b, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<UserDto> logout(@RequestBody LogoutRequestDto logoutRequestDto){
        User user = authService.logout(logoutRequestDto.getEmail());
        return new ResponseEntity<>(from(user), HttpStatus.OK);
    }

    private UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
