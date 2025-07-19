package com.musicapp.authservice.controller;

import com.musicapp.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth/admin")
public class AdminController {
    private final AuthService authService;

    @Autowired
    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public void makeAdmin(@RequestParam("id") UUID id) {
        authService.makeAdmin(id);
    }
}
