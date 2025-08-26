package com.food.delivery.service;

import com.food.delivery.dto.UserResponse;
import com.food.delivery.dto.UserUpdateRequest;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    User createUser(String email, String password, String name, UserRole role);


    User getUserEntityById(UUID userId);


    UserResponse getUserById(UUID userId);


    Page<UserResponse> getAllUsers(Pageable pageable);


    Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable);


    Page<UserResponse> getUsersByBlockedStatus(Boolean blocked, Pageable pageable);

    UserResponse updateUser(UUID userId, UserUpdateRequest request);


    void deleteUser(UUID userId);

    void blockUser(UUID userId);

    void unblockUser(UUID userId);
}
