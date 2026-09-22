package com.example.userauthservice.services;

import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.UserAlreadyExixtsException;
import com.example.userauthservice.models.State;
import com.example.userauthservice.models.User;
import com.example.userauthservice.repos.UserRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User signup(String email, String password) throws UserAlreadyExixtsException {
        Optional<User> userOptional = userRepo.findUserByEmail(email);
        if(userOptional.isPresent()) {
            throw new UserAlreadyExixtsException("User already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setState(State.ACTIVE);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepo.save(user);
        return user;
    }

    @Override
    public Pair<User, MultiValueMap<String, String>> login(String email, String password) throws InvalidCredentialsException {
        Optional<User> userOptional = userRepo.findUserByEmail(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
                throw new InvalidCredentialsException("Invalid credentials");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("email", user.getEmail());
            claims.put("roles", user.getRoles());

            long timeInMillis = System.currentTimeMillis();

            claims.put("iat", timeInMillis);
            claims.put("exp", timeInMillis + 1000 * 60 * 60 * 24); // 1 day expiration
            claims.put("iss", "userauthservice");
            MacAlgorithm algorithm = Jwts.SIG.HS256;
            SecretKey secretKey = algorithm.key().build();

            String token = Jwts.builder().claims(claims).signWith(secretKey).compact();
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add(HttpHeaders.SET_COOKIE, token);
            return new Pair<>(user, headers);
        }
        return null;
    }

    @Override
    public User logout(String email) {
        return null;
    }
}
