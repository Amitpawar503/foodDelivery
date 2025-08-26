package com.food.delivery.service;

import com.food.delivery.dto.AuthRequest;
import com.food.delivery.dto.AuthResponse;
import com.food.delivery.dto.LoginRequest;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.security.JwtUtil;
import com.food.delivery.service.impl.AuthServiceImpl;
import com.food.delivery.constants.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequest authRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");
        authRequest.setName("Test User");
        authRequest.setRole(UserRole.CUSTOMER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(java.util.UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(UserRole.CUSTOMER);
    }

    @Test
    void register_ValidRequest_ReturnsAuthResponse() {
        // Arrange
        String expectedToken = "jwt-token";
        when(userService.createUser(anyString(), anyString(), anyString(), any(UserRole.class)))
                .thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(expectedToken);

        // Act
        AuthResponse response = authService.register(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getName(), response.getName());
        assertEquals(testUser.getRole().name(), response.getRole());
        assertEquals(ErrorConstants.REGISTRATION_SUCCESS_MESSAGE, response.getMessage());

        verify(userService).createUser(
                authRequest.getEmail(),
                authRequest.getPassword(),
                authRequest.getName(),
                authRequest.getRole()
        );
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() {
        // Arrange
        String expectedToken = "jwt-token";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.getUserEntityById(any())).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(expectedToken);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getName(), response.getName());
        assertEquals(testUser.getRole().name(), response.getRole());
        assertEquals(ErrorConstants.LOGIN_SUCCESS_MESSAGE, response.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).getUserById(any());
        verify(jwtUtil).generateToken(testUser);
    }
}
