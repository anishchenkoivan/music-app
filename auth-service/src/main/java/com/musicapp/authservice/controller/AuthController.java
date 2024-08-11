package com.musicapp.authservice.controller;

import com.musicapp.authservice.dto.request.JwtIssueRequest;
import com.musicapp.authservice.dto.request.JwtValidateRequest;
import com.musicapp.authservice.dto.request.UserUpdateRequest;
import com.musicapp.authservice.dto.response.ApiError;
import com.musicapp.authservice.exception.TokenIssueException;
import com.musicapp.authservice.exception.UserNotFoundException;
import com.musicapp.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth-service")
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

    @GetMapping("/get-token")
    public String IssueToken(@RequestBody JwtIssueRequest request) {
        return authService.issueToken(
                request.id(),
                request.password());
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestBody JwtValidateRequest request) {
        return authService.validateToken(request.token(), request.userId());
    }

    @PostMapping("/create-user")
    public void CreateUser(@RequestBody UserUpdateRequest request) {
        authService.createUser(
                request.id(),
                request.password()
        );
    }

    @PutMapping("/update-user/{id}")
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

    @ExceptionHandler
    public ResponseEntity<ApiError> TokenIssueExceptionHandler(TokenIssueException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }
}
