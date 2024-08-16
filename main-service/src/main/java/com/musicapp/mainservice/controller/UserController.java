package com.musicapp.mainservice.controller;

import com.musicapp.mainservice.dto.request.UserUpdateRequest;
import com.musicapp.mainservice.dto.response.ApiError;
import com.musicapp.mainservice.exception.AuthException;
import com.musicapp.mainservice.exception.UserServiceException;
import com.musicapp.mainservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test/{id}")
    public String testRequest(@PathVariable String id) {
        return id;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.ok(userService.getPublicUserDetails(id));
        } else {
            UUID authenticatedUserId = UUID.fromString(authentication.getPrincipal().toString());
            if (id.equals(authenticatedUserId)) {
                return ResponseEntity.ok(userService.getAllUserDetails(id));
            }
        }
        return ResponseEntity.ok(userService.getPublicUserDetails(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public void updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = UUID.fromString(authentication.getPrincipal().toString());
        if (authenticatedUserId.equals(id)) {
            userService.updateUser(id, request.userDetails(), request.password());
        }
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
