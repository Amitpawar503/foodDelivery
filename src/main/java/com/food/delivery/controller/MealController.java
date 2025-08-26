package com.food.delivery.controller;

import com.food.delivery.dto.MealRequest;
import com.food.delivery.dto.MealResponse;
import com.food.delivery.service.MealService;
import com.food.delivery.constants.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
@Slf4j
public class MealController {

    @Autowired
    private MealService mealService;

    @PostMapping("/create/restaurant/{restaurantId}")
    public ResponseEntity<MealResponse> createMeal(@PathVariable UUID restaurantId,
                                                  @Valid @RequestBody MealRequest request,
                                                  Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Meal creation request received for restaurant: {} by user: {} with name: {}", 
                restaurantId, currentUserId, request.getName());
        try {
            MealResponse response = mealService.createMeal(restaurantId, request, currentUserId);
            log.info("Meal created successfully with ID: {} in restaurant: {} by user: {}", 
                    response.getId(), restaurantId, currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Meal creation failed for restaurant: {} by user: {} - Error: {}", 
                    restaurantId, currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get/restaurant/{restaurantId}")
    public ResponseEntity<Page<MealResponse>> getMealsByRestaurant(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get meals request received for restaurant: {} - page: {}, size: {}", restaurantId, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MealResponse> response = mealService.getMealsByRestaurant(restaurantId, pageable);
            log.info("Retrieved {} meals for restaurant: {}", response.getContent().size(), restaurantId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve meals for restaurant: {} - Error: {}", restaurantId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/search/restaurant/{restaurantId}")
    public ResponseEntity<Page<MealResponse>> searchMealsByRestaurant(
            @PathVariable UUID restaurantId,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Meal search request received for restaurant: {} - query: {}, page: {}, size: {}", 
                restaurantId, q, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MealResponse> response = mealService.searchMealsByRestaurant(restaurantId, q, pageable);
            log.info("Search completed for restaurant: {} - query: {} - found {} meals", 
                    restaurantId, q, response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Meal search failed for restaurant: {} - query: {} - Error: {}", 
                    restaurantId, q, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/price-range/restaurant/{restaurantId}")
    public ResponseEntity<Page<MealResponse>> getMealsByPriceRange(
            @PathVariable UUID restaurantId,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Price range meal request received for restaurant: {} - min: {}, max: {}, page: {}, size: {}", 
                restaurantId, minPrice, maxPrice, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MealResponse> response = mealService.getMealsByPriceRange(restaurantId, minPrice, maxPrice, pageable);
            log.info("Price range search completed for restaurant: {} - found {} meals", 
                    restaurantId, response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Price range meal search failed for restaurant: {} - Error: {}", restaurantId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MealResponse> getMeal(@PathVariable UUID id) {
        log.info("Get meal request received for ID: {}", id);
        try {
            MealResponse response = mealService.getMealById(id);
            log.info("Meal retrieved successfully with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve meal with ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<MealResponse> editMeal(@PathVariable UUID id,
                                                @Valid @RequestBody MealRequest request,
                                                Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Meal update request received for ID: {} by user: {}", id, currentUserId);
        try {
            MealResponse response = mealService.updateMeal(id, request, currentUserId);
            log.info("Meal updated successfully with ID: {} by user: {}", id, currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Meal update failed for ID: {} by user: {} - Error: {}", id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable UUID id, Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Meal deletion request received for ID: {} by user: {}", id, currentUserId);
        try {
            mealService.deleteMeal(id, currentUserId);
            log.info("Meal deleted successfully with ID: {} by user: {}", id, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Meal deletion failed for ID: {} by user: {} - Error: {}", id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }
}
