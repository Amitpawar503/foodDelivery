package com.food.delivery.controller;

import com.food.delivery.dto.UserResponse;
import com.food.delivery.dto.UserUpdateRequest;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role) {
        log.info("Admin get all users request received - page: {}, size: {}, role: {}", page, size, role);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserResponse> response = role != null ? 
                    userService.getUsersByRole(com.food.delivery.enums.UserRole.valueOf(role), pageable) :
                    userService.getAllUsers(pageable);
            log.info("Retrieved {} users for admin", response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve users for admin - Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        log.info("Admin user retrieval request received for ID: {}", id);
        try {
            UserResponse response = userService.getUserById(id);
            log.info("Admin user retrieved successfully with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Admin user retrieval failed for ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<UserResponse> editUser(@PathVariable UUID id,
                                                @Valid @RequestBody UserUpdateRequest request) {
        log.info("Admin user update request received for ID: {} - name: {}, role: {}, blocked: {}", 
                id, request.getName(), request.getRole(), request.getBlocked());
        try {
            UserResponse response = userService.updateUser(id, request);
            log.info("Admin user updated successfully with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Admin user update failed for ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Admin user deletion request received for ID: {}", id);
        try {
            userService.deleteUser(id);
            log.info("Admin user deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Admin user deletion failed for ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/create/block/{id}")
    public ResponseEntity<Void> createBlock(@PathVariable UUID id) {
        log.info("Admin user block request received for ID: {}", id);
        try {
            userService.blockUser(id);
            log.info("Admin user blocked successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Admin user block failed for ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/create/unblock/{id}")
    public ResponseEntity<Void> createUnblock(@PathVariable UUID id) {
        log.info("Admin user unblock request received for ID: {}", id);
        try {
            userService.unblockUser(id);
            log.info("Admin user unblocked successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Admin user unblock failed for ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
