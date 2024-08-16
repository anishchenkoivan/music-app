package com.musicapp.mainservice.service;

import com.musicapp.mainservice.dto.UserDetailsDto;
import com.musicapp.mainservice.dto.response.PublicUserDetailsResponse;
import com.musicapp.mainservice.service.gateway.AuthGateway;
import com.musicapp.mainservice.service.gateway.UserGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    UserGateway userGateway;
    AuthGateway authGateway;

    @Autowired
    public UserService(UserGateway userGateway, AuthGateway authGateway) {
        this.userGateway = userGateway;
        this.authGateway = authGateway;
    }

    public UserDetailsDto getAllUserDetails(UUID userId) {
        return userGateway.getAllUserDetails(userId);
    }

    public PublicUserDetailsResponse getPublicUserDetails(UUID userId) {
        return userGateway.getPublicUserDetails(userId);
    }

    public void updateUser(UUID userId, UserDetailsDto userDetailsDto, String password) {
        userGateway.updateUser(userId, userDetailsDto);
        authGateway.updateUser(userId, password);
    }
}
