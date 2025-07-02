package com.musicapp.userservice.controller;

import com.musicapp.userservice.dto.request.GetUserIdRequest;
import com.musicapp.userservice.dto.request.UserCreateRequest;
import com.musicapp.userservice.dto.request.UserModifyRequest;
import com.musicapp.userservice.dto.response.ApiError;
import com.musicapp.userservice.exception.CreateException;
import com.musicapp.userservice.exception.ValidateException;
import com.musicapp.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-user")
    public UUID createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}/update")
    public void updateUser(@PathVariable UUID id, @RequestBody UserModifyRequest request) {
        userService.updateUser(id, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.ok(userService.getPublicUserDetails(id));
        } else {
            UUID authenticatedUserId = UUID.fromString(authentication.getPrincipal().toString());
            if (id.equals(authenticatedUserId)) {
                return ResponseEntity.ok(userService.getUser(id).toDto());
            }
        }
        return ResponseEntity.ok(userService.getPublicUserDetails(id));
    }

    @PostMapping("/get-id")
    public UUID getIdByEmailOrUsername(@RequestBody GetUserIdRequest request) {
        return userService.getId(request.email(), request.username());
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> ValidateExceptionHandler(ValidateException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> NoSuchElementExceptionHandler(NoSuchElementException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> CreateExceptionHandler(CreateException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
