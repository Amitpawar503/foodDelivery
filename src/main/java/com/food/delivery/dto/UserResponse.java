package com.food.delivery.dto;

import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class UserResponse {
    UUID id;
    String email;
    String name;
    UserRole role;
    Boolean blocked;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
