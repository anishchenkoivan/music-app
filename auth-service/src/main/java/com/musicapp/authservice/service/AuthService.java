package com.musicapp.authservice.service;

import com.musicapp.authservice.entity.User;
import com.musicapp.authservice.exception.TokenInvalidException;
import com.musicapp.authservice.exception.TokenIssueException;
import com.musicapp.authservice.exception.UserNotFoundException;
import com.musicapp.authservice.repository.UserRepository;
import com.musicapp.authservice.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public void createUser(UUID id, String password) {
        User user = new User(id, passwordEncoder.encode(password));

        userRepository.save(user);
    }

    @Transactional
    public void ModifyUser(UUID id, String password) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Failed to find user with id: " + id));
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String issueToken(UUID id, String password) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Failed to find user with id: " + id));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new TokenIssueException("Failed to issue token, password is incorrect");
        }

        return jwtService.generateToken(user.getId());
    }

    @Transactional(readOnly = true)
    public UUID validateToken(String token) {
        UUID userId = jwtService.getUserId(token);
        boolean userExists = userRepository.existsById(userId);
        if (!jwtService.isValidToken(token) || !userExists) {
            throw new TokenInvalidException("Token is not valid");
        }
        return userId;
    }
}
