package com.food.delivery.service;

import com.food.delivery.dto.AuthRequest;
import com.food.delivery.dto.AuthResponse;
import com.food.delivery.dto.LoginRequest;

public interface AuthService {

    AuthResponse register(AuthRequest request);

    AuthResponse login(LoginRequest request);
}
