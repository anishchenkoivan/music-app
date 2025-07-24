package com.musicapp.musicservice.gateway;

import com.musicapp.musicservice.dto.request.JwtValidateRequest;
import com.musicapp.musicservice.dto.response.TokenValidateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "auth-service",
        path = "/auth"
)
public interface AuthClient {
    @GetMapping("/validate")
    TokenValidateResponse validateToken(JwtValidateRequest request);
}
