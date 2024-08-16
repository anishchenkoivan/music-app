package com.musicapp.mainservice.security;

import com.musicapp.mainservice.dto.request.JwtValidateRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private  final RestTemplate restTemplate;
    private final String authServiceUrl;

    @Autowired
    public JwtAuthenticationFilter(RestTemplate restTemplate, @Value("${service.auth.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String token = extractToken(request);
        UUID userId = validateTokenAndGetUserDetails(token);
        if (token != null && userId != null) {
            var auth = new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private UUID validateTokenAndGetUserDetails(String token) {
        String validateUrl = authServiceUrl + "/validate";
        try {
            ResponseEntity<UUID> userId = restTemplate.postForEntity(validateUrl, new JwtValidateRequest(token), UUID.class);
            if (userId.getStatusCode().is2xxSuccessful()) {
                return userId.getBody();
            }
        } catch(RestClientException e) {
            return null;
        }
        return null;
    }
}
