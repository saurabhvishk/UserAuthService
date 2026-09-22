package com.example.userauthservice.advisors;

import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.UserAlreadyExixtsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler({UserAlreadyExixtsException.class,IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<String> handleExceptionUserAlreadyExist(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidCredentialsException.class})
    public ResponseEntity<String> handleExceptionInvalidCredentials(Exception exception) {
        System.out.println("InvalidCredentialsException: " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
