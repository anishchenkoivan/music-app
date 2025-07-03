package com.musicapp.userservice.gateway;

import com.musicapp.userservice.dto.request.JwtValidateRequest;
import com.musicapp.userservice.dto.request.UserSecurityModifyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(
        name = "auth-service",
        path = "/auth"
)
public interface AuthClient {

    @GetMapping("/validate")
    UUID validateToken(JwtValidateRequest request);

    @PostMapping("/create-user")
    void createUser(UserSecurityModifyRequest request);
}
