package com.food.delivery.controller;

import com.food.delivery.service.UserRestaurantBlockService;
import com.food.delivery.constants.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/owner/blocks")
@Slf4j
public class OwnerBlockController {

    @Autowired
    private UserRestaurantBlockService userRestaurantBlockService;

    @PostMapping("/create/restaurant/{restaurantId}/user/{userId}")
    public ResponseEntity<Void> createBlock(@PathVariable UUID restaurantId, @PathVariable UUID userId, Authentication authentication) {
        UUID ownerId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("User block request received for restaurant: {} and user: {} by owner: {}", restaurantId, userId, ownerId);
        try {
            userRestaurantBlockService.blockUser(restaurantId, userId, ownerId);
            log.info("User blocked successfully for restaurant: {} and user: {} by owner: {}", restaurantId, userId, ownerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("User block failed for restaurant: {} and user: {} by owner: {} - Error: {}", 
                    restaurantId, userId, ownerId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete/restaurant/{restaurantId}/user/{userId}")
    public ResponseEntity<Void> deleteBlock(@PathVariable UUID restaurantId, @PathVariable UUID userId, Authentication authentication) {
        UUID ownerId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("User unblock request received for restaurant: {} and user: {} by owner: {}", restaurantId, userId, ownerId);
        try {
            userRestaurantBlockService.unblockUser(restaurantId, userId, ownerId);
            log.info("User unblocked successfully for restaurant: {} and user: {} by owner: {}", restaurantId, userId, ownerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("User unblock failed for restaurant: {} and user: {} by owner: {} - Error: {}", 
                    restaurantId, userId, ownerId, e.getMessage(), e);
            throw e;
        }
    }
}
