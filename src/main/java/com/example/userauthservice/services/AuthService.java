package com.example.userauthservice.services;

import com.example.userauthservice.dtos.LoginResult;
import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.InvalidTokenException;
import com.example.userauthservice.exception.UserAlreadyExistsException;
import com.example.userauthservice.models.*;
import com.example.userauthservice.repos.RoleRepo;
import com.example.userauthservice.repos.SessionRepo;
import com.example.userauthservice.repos.UserRepo;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService{
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private SecretKey secretKey;

    private static final long TOKEN_VALIDITY_MS = 24 * 60 * 60 * 1000L; // 24 hours

    @Override
    public User signup(String email, String password) throws UserAlreadyExistsException {
        Optional<User> userOptional = userRepo.findUserByEmail(email);
        if(userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setState(State.ACTIVE);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.getRoles().add(getOrCreateRole(RoleType.USER));
        userRepo.save(user);
        return user;
    }

    @Override
    public LoginResult login(String email, String password) throws InvalidCredentialsException {
        User user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        if (user.getState() != State.ACTIVE) {
            throw new InvalidCredentialsException("Account is not active");
        }

        String token = createToken(user);

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setState(State.ACTIVE);
        sessionRepo.save(session);

        return new LoginResult(user, token);
    }

    @Override
    public void logout(String token, Long userId) throws InvalidTokenException {
        Session session = sessionRepo.findByTokenAndUserId(token, userId)
                .orElseThrow(() -> new InvalidTokenException("Session not found"));
        session.setState(State.INACTIVE);
        sessionRepo.save(session);
    }


    @Override
    public Boolean validateToken(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepo.findByTokenAndUserId(token, userId);
        if(sessionOptional.isEmpty() || sessionOptional.get().getState() != State.ACTIVE) {
            return false;
        }
        try{
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String createToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + TOKEN_VALIDITY_MS);

        List<RoleType> roleNames = user.getRoles().stream()
                .map(Role::getValue)
                .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("roles", roleNames)
                .issuer("userAuthService")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }


    private Role getOrCreateRole(RoleType value) {
        return roleRepo.findByValue(value).orElseGet(() -> {
            Role role = new Role();
            role.setValue(value);
            role.setState(State.ACTIVE);
            return roleRepo.save(role);
        });
    }
}
