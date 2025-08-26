package com.food.delivery.service.impl;

import com.food.delivery.dto.UserResponse;
import com.food.delivery.dto.UserUpdateRequest;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.UserRepository;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(String email, String password, String name, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(ErrorConstants.USER_EMAIL_EXISTS_MESSAGE + ": " + email);
        }

        User user = new User();
        user.setId(UUID.nameUUIDFromBytes(email.getBytes()));
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setBlocked(false);

        User savedUser = userRepository.save(user);
        log.info("Created user: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User getUserEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.USER_NOT_FOUND_MESSAGE + ": " + userId));
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        User user = getUserEntityById(userId);
        return mapToUserResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }

    @Override
    public Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable) {
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(this::mapToUserResponse);
    }

    @Override
    public Page<UserResponse> getUsersByBlockedStatus(Boolean blocked, Pageable pageable) {
        Page<User> users = userRepository.findByBlocked(blocked, pageable);
        return users.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        User user = getUserEntityById(userId);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getBlocked() != null) {
            user.setBlocked(request.getBlocked());
        }

        User savedUser = userRepository.save(user);
        log.info("Updated user: {}", savedUser.getEmail());
        return mapToUserResponse(savedUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = getUserEntityById(userId);
        userRepository.deleteById(userId);
        log.info("Deleted user: {}", user.getEmail());
    }

    @Override
    public void blockUser(UUID userId) {
        User user = getUserEntityById(userId);
        user.setBlocked(true);
        userRepository.save(user);
        log.info("Blocked user: {}", user.getEmail());
    }

    @Override
    public void unblockUser(UUID userId) {
        User user = getUserEntityById(userId);
        user.setBlocked(false);
        userRepository.save(user);
        log.info("Unblocked user: {}", user.getEmail());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .blocked(user.getBlocked())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
