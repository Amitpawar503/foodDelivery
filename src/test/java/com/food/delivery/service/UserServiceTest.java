package com.food.delivery.service;

import com.food.delivery.dto.UserResponse;
import com.food.delivery.dto.UserUpdateRequest;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.UserRepository;
import com.food.delivery.service.impl.UserServiceImpl;
import com.food.delivery.constants.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setName("Test User");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setBlocked(false);
    }

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUserDetails() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userService.getUserEntityById(UUID.randomUUID());

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_NonExistingUser_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.getUserById(UUID.randomUUID()));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void createUser_ValidData_ReturnsUser() {
        // Arrange
        String email = "new@example.com";
        String password = "password123";
        String name = "New User";
        UserRole role = UserRole.CUSTOMER;
        String encodedPassword = "encodedPassword123";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(email, password, name, role);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ExistingEmail_ThrowsException() {
        // Arrange
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(email, "password", "name", UserRole.CUSTOMER));
        assertEquals(ErrorConstants.USER_EMAIL_EXISTS_MESSAGE + ": " + email, exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserEntityById_ExistingUser_ReturnsUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserEntityById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserEntityById_NonExistingUser_ThrowsException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserEntityById(nonExistingId));
        assertEquals(ErrorConstants.USER_NOT_FOUND_MESSAGE + ": " + nonExistingId, exception.getMessage());
        verify(userRepository).findById(nonExistingId);
    }

    @Test
    void getAllUsers_ReturnsPageOfUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(testUser);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<UserResponse> result = userService.getAllUsers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testUser.getEmail(), result.getContent().get(0).getEmail());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void updateUser_ValidRequest_ReturnsUpdatedUser() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Updated Name");
        request.setRole(UserRole.OWNER);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("test@example.com");
        updatedUser.setName("Updated Name");
        updatedUser.setRole(UserRole.OWNER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserResponse result = userService.updateUser(userId, request);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Updated Name", result.getName());
        assertEquals(UserRole.OWNER, result.getRole());
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ExistingUser_DeletesUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void blockUser_ExistingUser_BlocksUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.blockUser(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void unblockUser_ExistingUser_UnblocksUser() {
        // Arrange
        testUser.setBlocked(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.unblockUser(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }
}
