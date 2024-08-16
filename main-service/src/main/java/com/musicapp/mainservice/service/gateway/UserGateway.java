package com.musicapp.mainservice.service.gateway;

import com.musicapp.mainservice.dto.UserDetailsDto;
import com.musicapp.mainservice.dto.request.GetUserIdRequest;
import com.musicapp.mainservice.dto.response.PublicUserDetailsResponse;

import java.util.UUID;

public interface UserGateway {
    UUID createUser(UserDetailsDto userDetailsDto);
    void updateUser(UUID id, UserDetailsDto userDetailsDto);
    UserDetailsDto getAllUserDetails(UUID userId);
    PublicUserDetailsResponse getPublicUserDetails(UUID id);
    UUID getIdByEmailOrUsername(String email, String username);
}
