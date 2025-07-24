package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.request.JwtValidateRequest;
import com.musicapp.musicservice.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/security")
public class SecurityController {
    private final JwtService jwtService;

    @Autowired
    public SecurityController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/validate")
    public void validateToken(@RequestBody JwtValidateRequest request) {
        if (!jwtService.isValidToken(request.token())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
