package com.musicapp.userservice.service;

import com.musicapp.userservice.dto.PublicUserDetailsDto;
import com.musicapp.userservice.dto.request.UserModifyRequest;
import com.musicapp.userservice.entity.User;
import com.musicapp.userservice.exception.ValidateException;
import com.musicapp.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @Transactional
    public UUID createUser(UserModifyRequest userData) {
        User user = new User(
                userData.firstName(),
                userData.lastName(),
                userData.username(),
                userData.bio(),
                userData.country(),
                userData.email(),
                userData.profilePicture());
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PublicUserDetailsDto getPublicUserDetails(UUID id) {
        User user = userRepository.findById(id).orElseThrow();
        return user.getPublicUserDetails();
    }

    @Transactional
    public void updateUser(UUID id, UserModifyRequest userData) {
        User user = userRepository.findById(id).orElseThrow();
        validateUserDetails(userData);
        setUserDetails(user, userData);
    }

    @Transactional(readOnly = true)
    public UUID getId(String email, String username) {
        if (isNullOrEmpty(email) && isNullOrEmpty(username)) {
            throw new ValidateException("Email or Username is required");
        }

        if (!isNullOrEmpty(email)) {
            return userRepository.findByEmail(email).orElseThrow().getId();
        } else {
            return userRepository.findByUsername(username).orElseThrow().getId();
        }
    }

    private void validateUserDetails(UserModifyRequest userData) {
        if (isUsernameTaken(userData.username())) {
            throw new ValidateException("Username taken");
        }
    }

    private boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    private void setUserDetails(User user, UserModifyRequest userData) {
        user.setFirstName(userData.firstName());
        user.setLastName(userData.lastName());
        user.setUsername(userData.username());
        user.setCountry(userData.country());
        user.setEmail(userData.email());
        user.setProfilePicture(userData.profilePicture());
        user.setBio(userData.bio());
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
