package com.musicapp.mainservice.controller;

import com.musicapp.mainservice.dto.AuthDto;
import com.musicapp.mainservice.dto.request.LoginRequest;
import com.musicapp.mainservice.dto.request.UserUpdateRequest;
import com.musicapp.mainservice.dto.response.ApiError;
import com.musicapp.mainservice.exception.AuthException;
import com.musicapp.mainservice.exception.UserServiceException;
import com.musicapp.mainservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test/{id}")
    public Long test(@PathVariable Long id) {
        return id;
    }

    @PostMapping("/login")
    public AuthDto login(@RequestBody LoginRequest loginRequest) {
        return authService.login(
                loginRequest.email(),
                loginRequest.username(),
                loginRequest.password());
    }

    @PostMapping("/register")
    public AuthDto Register(@RequestBody UserUpdateRequest userUpdateRequest) {
        return authService.register(userUpdateRequest.userDetails(), userUpdateRequest.password());
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> AuthExceptionHandler(AuthException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                e.getStatusCode() != null ? e.getStatusCode() : HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> AuthExceptionHandler(UserServiceException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                e.getStatusCode() != null ? e.getStatusCode() : HttpStatus.BAD_REQUEST
        );
    }
}
