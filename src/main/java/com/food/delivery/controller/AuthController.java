package com.food.delivery.controller;

import com.food.delivery.dto.AuthRequest;
import com.food.delivery.dto.AuthResponse;
import com.food.delivery.dto.LoginRequest;
import com.food.delivery.service.AuthService;
import com.food.delivery.constants.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    @Operation(summary = "Register new user", description = "Creates a new user account (Customer or Owner)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        log.info("User registration request received for email: {} with role: {}", request.getEmail(), request.getRole());
        try {
            AuthResponse response = authService.register(request);
            log.info("User registered successfully with email: {} and role: {}", request.getEmail(), request.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("User registration failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/get")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "User is blocked")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login request received for email: {}", request.getEmail());
        try {
            AuthResponse response = authService.login(request);
            log.info("User login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("User login failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/debug/password")
    @Operation(summary = "Debug password encoding", description = "Test password encoding and matching")
    public ResponseEntity<Map<String, Object>> debugPassword() {
        Map<String, Object> response = new HashMap<>();

        try {
            String testPassword = "admin123";
            String encodedPassword = passwordEncoder.encode(testPassword);

            // Test with the hash from Flyway migration
            String flywayHash = "$2a$10$ZKAPwbAfDjaTfNBCLey0.unkP.xcayCAKxReE7.qsDZ85pAMaFydC";

            boolean matchesFlyway = passwordEncoder.matches(testPassword, flywayHash);

            // Test with newly encoded password
            boolean matchesNew = passwordEncoder.matches(testPassword, encodedPassword);

            response.put("testPassword", testPassword);
            response.put("newEncodedPassword", encodedPassword);
            response.put("flywayHash", flywayHash);
            response.put("matchesFlyway", matchesFlyway);
            response.put("matchesNew", matchesNew);

            log.info("Password encoding test - matchesFlyway: {}, matchesNew: {}", matchesFlyway, matchesNew);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            log.error("Error testing password encoding: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(response);
    }
}
