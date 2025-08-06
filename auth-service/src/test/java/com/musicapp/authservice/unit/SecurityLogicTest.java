package com.musicapp.authservice.unit;

import com.musicapp.authservice.entity.User;
import com.musicapp.authservice.exception.TokenIssueException;
import com.musicapp.authservice.repository.UserRepository;
import com.musicapp.authservice.security.JwtService;
import com.musicapp.authservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {
        AuthService.class,
})
@Import({JwtService.class, SecurityLogicTest.TestConfig.class})
public class SecurityLogicTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void shouldIssueAndValidateToken() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, passwordEncoder.encode("password"));

        doReturn(Optional.of(user)).when(userRepository).findById(eq(userId));

        String token = authService.issueToken(userId, "password");
        boolean isValid = authService.validateToken(token).getId().equals(userId);

        assertTrue(isValid);
    }

    @Test
    public void shouldNotIssueTokenWhenPasswordIsInvalid() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, passwordEncoder.encode("password"));

        doReturn(Optional.of(user)).when(userRepository).findById(eq(userId));

        assertThrows(TokenIssueException.class, () -> authService.issueToken(userId, "wrong password"));
    }
}
