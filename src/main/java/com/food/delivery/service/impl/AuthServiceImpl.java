package com.food.delivery.service.impl;

import com.food.delivery.dto.AuthRequest;
import com.food.delivery.dto.AuthResponse;
import com.food.delivery.dto.LoginRequest;
import com.food.delivery.entity.User;
import com.food.delivery.security.JwtUtil;
import com.food.delivery.service.AuthService;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponse register(AuthRequest request) {
        User user = userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getRole()
        );

        String token = jwtUtil.generateToken(user);
        log.info("User registered successfully: {}", user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name(), ErrorConstants.REGISTRATION_SUCCESS_MESSAGE);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user);
            log.info("User logged in successfully: {} with role: {}", user.getEmail(), user.getRole());

            return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name(), ErrorConstants.LOGIN_SUCCESS_MESSAGE);
        } catch (Exception e) {
            log.error("Authentication failed for user: {} - Error: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }
}
