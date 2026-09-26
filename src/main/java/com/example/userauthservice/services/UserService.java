package com.example.userauthservice.services;

import com.example.userauthservice.exception.InvalidCredentialsException;
import com.example.userauthservice.exception.UserNotFoundException;
import com.example.userauthservice.models.Session;
import com.example.userauthservice.models.State;
import com.example.userauthservice.models.User;
import com.example.userauthservice.repos.SessionRepo;
import com.example.userauthservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User getProfile(Long userId) throws UserNotFoundException {
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User updateProfile(Long userId, String name, String phone) throws UserNotFoundException {
        User user = getProfile(userId);
        // only change the fields that were sent
        if (name != null) {
            user.setName(name);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentToken, String oldPassword, String newPassword)
            throws UserNotFoundException, InvalidCredentialsException {
        User user = getProfile(userId);

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepo.save(user);

        // log out every other device, keep the current one
        for (Session session : sessionRepo.findAllByUserIdAndState(userId, State.ACTIVE)) {
            if (!session.getToken().equals(currentToken)) {
                session.setState(State.INACTIVE);
                sessionRepo.save(session);
            }
        }
    }
}
