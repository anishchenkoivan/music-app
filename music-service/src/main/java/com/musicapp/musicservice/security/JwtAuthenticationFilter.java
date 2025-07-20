package com.musicapp.musicservice.security;

import com.musicapp.musicservice.dto.request.JwtValidateRequest;
import com.musicapp.musicservice.dto.response.TokenValidateResponse;
import com.musicapp.musicservice.gateway.AuthClient;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthClient authClient;

    @Autowired
    public JwtAuthenticationFilter(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String token = extractToken(request);
        TokenValidateResponse details = validateTokenAndGetUserDetails(token);
        if (token != null && details != null) {
            List<SimpleGrantedAuthority> authorities = details.roles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
            var auth = new UsernamePasswordAuthenticationToken(details.id(), null, authorities);
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

    private TokenValidateResponse validateTokenAndGetUserDetails(String token) {
        try {
            return authClient.validateToken(new JwtValidateRequest(token));
        } catch(FeignException e) {
            return null;
        }
    }
}
