package com.musicapp.mainservice.service.gateway;

import java.util.UUID;

public interface AuthGateway {
    String issueToken(UUID id, String password);
    void createUser(UUID id, String password);
    void updateUser(UUID id, String password);
}
