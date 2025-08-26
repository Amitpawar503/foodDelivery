package com.food.delivery.controller;

import com.food.delivery.dto.RestaurantResponse;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.constants.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/restaurants")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/get")
    public ResponseEntity<Page<RestaurantResponse>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("Admin get all restaurants request received - page: {}, size: {}, sort: {}, direction: {}", 
                page, size, sort, direction);
        try {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<RestaurantResponse> response = restaurantService.getAllRestaurants(pageable);
            log.info("Retrieved {} restaurants for admin", response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve restaurants for admin - Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/create/block/{id}")
    public ResponseEntity<Void> createBlock(@PathVariable UUID id, Authentication authentication) {
        UUID adminId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Admin restaurant block request received for ID: {} by admin: {}", id, adminId);
        try {
            restaurantService.blockRestaurant(id, adminId);
            log.info("Admin restaurant blocked successfully with ID: {} by admin: {}", id, adminId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Admin restaurant block failed for ID: {} by admin: {} - Error: {}", id, adminId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/create/unblock/{id}")
    public ResponseEntity<Void> createUnblock(@PathVariable UUID id, Authentication authentication) {
        UUID adminId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Admin restaurant unblock request received for ID: {} by admin: {}", id, adminId);
        try {
            restaurantService.unblockRestaurant(id, adminId);
            log.info("Admin restaurant unblocked successfully with ID: {} by admin: {}", id, adminId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Admin restaurant unblock failed for ID: {} by admin: {} - Error: {}", id, adminId, e.getMessage(), e);
            throw e;
        }
    }
}
