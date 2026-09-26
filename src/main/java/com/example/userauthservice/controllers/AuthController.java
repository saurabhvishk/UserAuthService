package com.example.userauthservice.controllers;

import com.example.userauthservice.dtos.*;
import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.InvalidTokenException;
import com.example.userauthservice.exception.UserAlreadyExistsException;
import com.example.userauthservice.models.User;
import com.example.userauthservice.services.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) throws UserAlreadyExistsException {
        User user = authService.signup(signupRequestDto.getEmail(),signupRequestDto.getPassword());
        return new ResponseEntity<>(UserDto.from(user), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) throws InvalidCredentialsException {
        LoginResult result = authService.login(loginRequestDto.getEmail(),loginRequestDto.getPassword());
        ResponseCookie cookie = ResponseCookie.from("auth-token", result.token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(1)) // 1 day
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(UserDto.from(result.user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequestDto logoutRequestDto) throws InvalidTokenException {
        authService.logout(logoutRequestDto.getToken(), logoutRequestDto.getUserId());
        ResponseCookie clearCookie = ResponseCookie.from("auth-token", "")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenDto validateTokenDto) {
        Boolean isValid = authService.validateToken(validateTokenDto.getToken(), validateTokenDto.getUserId());
        return new ResponseEntity<>(isValid, isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

}
