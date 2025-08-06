package com.musicapp.userservice.integration;

import com.musicapp.userservice.dto.request.UserCreateRequest;
import com.musicapp.userservice.dto.request.UserModifyRequest;
import com.musicapp.userservice.dto.request.UserSecurityModifyRequest;
import com.musicapp.userservice.entity.User;
import com.musicapp.userservice.gateway.AuthClient;
import com.musicapp.userservice.repository.UserRepository;
import com.musicapp.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceIntegrationTest {
    @Container
    @ServiceConnection
    public final static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AuthClient authClient;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void testCrud() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                new UserModifyRequest(
                        "First",
                        "Last",
                        "username",
                        "bio",
                        "US",
                        "email@gmail.com",
                        "pictureUrl"
                ),
                "password"
        );

        doNothing().when(authClient).createUser(any(UserSecurityModifyRequest.class));

        UUID userId = userService.createUser(userCreateRequest);
        User user = userService.getUser(userId);
        assertEquals("First", user.getFirstName());
        assertEquals("Last", user.getLastName());
        assertEquals("username", user.getUsername());

        userService.updateUser(userId, new UserModifyRequest(
                "New First",
                "New Last",
                "New Username",
                "New Bio",
                "RU",
                "New Email",
                "New PictureUrl"
        ));

        User updatedUser = userService.getUser(userId);

        assertEquals("New First", updatedUser.getFirstName());
        assertEquals("New Last", updatedUser.getLastName());
        assertEquals("New Username", updatedUser.getUsername());
    }
}
