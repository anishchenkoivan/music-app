package com.musicapp.authservice.security;

import com.musicapp.authservice.entity.Role;
import com.musicapp.authservice.entity.User;
import com.musicapp.authservice.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final AuthService authService;

    @Autowired
    public AuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        User user = validateTokenAndGetUserDetails(token);
        if (token != null && user != null) {
            Set<Role> roles = user.getRoles();

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .toList();

            var auth = new UsernamePasswordAuthenticationToken(user.getId(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private User validateTokenAndGetUserDetails(String token) {
        try {
            return authService.validateToken(token);
        } catch(RuntimeException e) {
            return null;
        }
    }
}
