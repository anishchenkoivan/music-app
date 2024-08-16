package com.musicapp.mainservice.service.gateway;

import com.musicapp.mainservice.dto.UserDetailsDto;
import com.musicapp.mainservice.dto.request.GetUserIdRequest;
import com.musicapp.mainservice.dto.response.PublicUserDetailsResponse;
import com.musicapp.mainservice.exception.UserServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class HttpUserGateway implements UserGateway {
    private final String userServiceUrl;
    private final RestTemplate restTemplate;


    @Autowired
    public HttpUserGateway(@Value("${service.user.url}") String userServiceUrl, RestTemplate restTemplate) {
        this.userServiceUrl = userServiceUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public UUID createUser(UserDetailsDto userDetailsDto) {
        try {
            ResponseEntity<UUID> response = restTemplate.postForEntity(
                    userServiceUrl + "/create-user",
                    userDetailsDto,
                    UUID.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new UserServiceException("Failed to create user, status code: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new UserServiceException("Failed to create user, reason: " + e.getMessage());
        }
    }

    @Override
    public void updateUser(UUID id, UserDetailsDto userDetailsDto) {
        try {
            restTemplate.put(
                    userServiceUrl + "/" + id + "/update",
                    userDetailsDto
            );
        } catch (HttpStatusCodeException e) {
            throw new UserServiceException("Failed to update user, status code: " + e.getStatusCode());
        } catch (RestClientException e) {
            throw new UserServiceException("Failed to update user, reason: " + e.getMessage());
        }
    }

    @Override
    public UserDetailsDto getAllUserDetails(UUID id) {
        try {
            ResponseEntity<UserDetailsDto> response = restTemplate.getForEntity(
                    userServiceUrl + "/" + id + "/all",
                    UserDetailsDto.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new UserServiceException("Failed to get all user details, status code: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new UserServiceException("Failed to get all user details, reason: " + e.getMessage());
        }
    }

    @Override
    public PublicUserDetailsResponse getPublicUserDetails(UUID id) {
        try {
            ResponseEntity<PublicUserDetailsResponse> response = restTemplate.getForEntity(
                    userServiceUrl + "/" + id + "/public",
                    PublicUserDetailsResponse.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new UserServiceException("Failed to get public user details, status code: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new UserServiceException("Failed to get public user details, reason: " + e.getMessage());
        }
    }

    @Override
    public UUID getIdByEmailOrUsername(String email, String username) {
        try {
            ResponseEntity<UUID> response = restTemplate.postForEntity(
                    userServiceUrl + "/get-id",
                    new GetUserIdRequest(username, email),
                    UUID.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new UserServiceException("Failed to get id, status code: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new UserServiceException("Failed to get id, reason: " + e.getMessage());
        }
    }
}
