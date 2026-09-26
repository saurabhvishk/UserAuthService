package com.example.userauthservice.controllers;

import com.example.userauthservice.dtos.ChangePasswordRequestDto;
import com.example.userauthservice.dtos.UpdateProfileRequestDto;
import com.example.userauthservice.dtos.UserDto;
import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.UserNotFoundException;
import com.example.userauthservice.models.User;
import com.example.userauthservice.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(Authentication authentication) throws UserNotFoundException {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getProfile(userId);
        return ResponseEntity.ok(UserDto.from(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateMyProfile(Authentication authentication,
                                                   @Valid @RequestBody UpdateProfileRequestDto request) throws UserNotFoundException {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.updateProfile(userId, request.getName(), request.getPhone());
        return ResponseEntity.ok(UserDto.from(user));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changeMyPassword(Authentication authentication,
                                                 @Valid @RequestBody ChangePasswordRequestDto request)
            throws UserNotFoundException, InvalidCredentialsException {
        Long userId = (Long) authentication.getPrincipal();
        String currentToken = (String) authentication.getCredentials();
        userService.changePassword(userId, currentToken, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
