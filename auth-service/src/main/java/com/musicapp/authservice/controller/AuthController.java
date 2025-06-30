package com.musicapp.authservice.controller;

import com.musicapp.authservice.dto.request.JwtIssueRequest;
import com.musicapp.authservice.dto.request.JwtValidateRequest;
import com.musicapp.authservice.dto.request.UserUpdateRequest;
import com.musicapp.authservice.dto.response.ApiError;
import com.musicapp.authservice.exception.TokenInvalidException;
import com.musicapp.authservice.exception.TokenIssueException;
import com.musicapp.authservice.exception.UserNotFoundException;
import com.musicapp.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @PostMapping("/get-token")
    public String IssueToken(@RequestBody JwtIssueRequest request) {
        return authService.issueToken(
                request.id(),
                request.password());
    }

    @PostMapping("/validate")
    public UUID validateToken(@RequestBody JwtValidateRequest request) {
        return authService.validateToken(request.token());
    }

    @PostMapping("/create-user")
    public void createUser(@RequestBody UserUpdateRequest request) {
        authService.createUser(
                request.id(),
                request.password()
        );
    }

    @PutMapping("/update-user")
    public void updateUser(@RequestBody UserUpdateRequest request) {
        authService.ModifyUser(
                request.id(),
                request.password()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> UserNotFoundExceptionHandler(UserNotFoundException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({TokenIssueException.class, TokenInvalidException.class})
    public ResponseEntity<ApiError> TokenIssueExceptionHandler(RuntimeException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }
}
