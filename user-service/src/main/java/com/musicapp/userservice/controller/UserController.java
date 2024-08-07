package com.musicapp.userservice.controller;

import com.musicapp.userservice.dto.PublicUserDetailsDto;
import com.musicapp.userservice.dto.request.GetUserIdRequest;
import com.musicapp.userservice.dto.request.UserModifyRequest;
import com.musicapp.userservice.dto.response.ApiError;
import com.musicapp.userservice.entity.User;
import com.musicapp.userservice.exception.ValidateException;
import com.musicapp.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
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

    @PostMapping("/new")
    public UUID createUser(@RequestBody UserModifyRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}/update")
    public void updateUser(@PathVariable UUID id, @RequestBody UserModifyRequest request) {
        userService.updateUser(id, request);
    }

    @GetMapping("/{id}/all")
    public User getAllUserDetails(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/public")
    public PublicUserDetailsDto getPublicUserDetails(@PathVariable UUID id) {
        return userService.getPublicUserDetails(id);
    }

    @GetMapping("/get-id")
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
}
