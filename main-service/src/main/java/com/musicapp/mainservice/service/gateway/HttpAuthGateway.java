package com.musicapp.mainservice.service.gateway;

import com.musicapp.mainservice.dto.request.JwtIssueRequest;
import com.musicapp.mainservice.dto.request.UserAuthUpdateRequest;
import com.musicapp.mainservice.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class HttpAuthGateway implements AuthGateway {
    private final String authServiceUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public HttpAuthGateway(@Value("${service.auth.url}") String authServiceUrl, RestTemplate restTemplate) {
        this.authServiceUrl = authServiceUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public String issueToken(UUID id, String password) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    authServiceUrl + "/get-token",
                    new JwtIssueRequest(id, password),
                    String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new AuthException("Failed to issue token, response: " + response.getBody(), response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new AuthException("Failed to issue token, reason: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UUID id, String password) {
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    authServiceUrl + "/create-user",
                    new UserAuthUpdateRequest(id, password),
                    Void.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AuthException("Failed to create user, response: " + response.getBody(), response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new AuthException("Failed to create user, reason: " + e.getMessage());
        }
    }

    @Override
    public void updateUser(UUID id, String password) {
        try {
            restTemplate.put(
                    authServiceUrl + "/update-user",
                    new UserAuthUpdateRequest(id, password)
            );
        } catch (HttpStatusCodeException e) {
            throw new AuthException("Failed to update user, reason: " + e.getMessage(), e.getStatusCode());
        } catch (RestClientException e) {
            throw new AuthException("Failed to create user, reason: " + e.getMessage());
        }
    }
}
