package com.musicapp.mainservice.service;

import com.musicapp.mainservice.dto.AuthDto;
import com.musicapp.mainservice.dto.UserDetailsDto;
import com.musicapp.mainservice.service.gateway.AuthGateway;
import com.musicapp.mainservice.service.gateway.UserGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    AuthGateway authGateway;
    UserGateway userGateway;

    @Autowired
    public AuthService(AuthGateway authGateway, UserGateway userGateway) {
        this.authGateway = authGateway;
        this.userGateway = userGateway;
    }

    public AuthDto login(String email, String username, String password) {
        UUID id = userGateway.getIdByEmailOrUsername(email, username);
        String token = authGateway.issueToken(id, password);
        return new AuthDto(id, token);
    }

    public AuthDto register(UserDetailsDto userDetailsDto, String password) {
        UUID id = userGateway.createUser(userDetailsDto);
        authGateway.createUser(id, password);
        String token = authGateway.issueToken(id, password);
        return new AuthDto(id, token);
    }
}
