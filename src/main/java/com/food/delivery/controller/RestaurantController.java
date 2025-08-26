package com.food.delivery.controller;

import com.food.delivery.dto.RestaurantRequest;
import com.food.delivery.dto.RestaurantResponse;
import com.food.delivery.entity.User;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.constants.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@Slf4j
@Tag(name = "Restaurants", description = "Restaurant management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/create")
    @Operation(summary = "Create a new restaurant", description = "Creates a new restaurant for the authenticated owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant created successfully",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest request,
                                                             Authentication authentication) {
        UUID ownerId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Restaurant creation request received for owner: {} with name: {}", ownerId, request.getName());
        try {
            RestaurantResponse response = restaurantService.createRestaurant(request, ownerId);
            log.info("Restaurant created successfully with ID: {} for owner: {}", response.getId(), ownerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Restaurant creation failed for owner: {} - Error: {}", ownerId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Get all restaurants", description = "Retrieves all active restaurants with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<Page<RestaurantResponse>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get all restaurants request received - page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<RestaurantResponse> response = restaurantService.getAllRestaurants(pageable);
            log.info("Retrieved {} restaurants for page: {}", response.getContent().size(), page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve restaurants - Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieves a specific restaurant by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant retrieved successfully",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable UUID id) {
        log.info("Get restaurant request received for ID: {}", id);
        try {
            RestaurantResponse response = restaurantService.getRestaurantById(id);
            log.info("Restaurant retrieved successfully with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve restaurant with ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search restaurants", description = "Searches restaurants by name or description")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<Page<RestaurantResponse>> searchRestaurants(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Restaurant search request received - query: {}, page: {}, size: {}", q, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<RestaurantResponse> response = restaurantService.searchRestaurants(q, pageable);
            log.info("Search completed for query: {} - found {} restaurants", q, response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Restaurant search failed for query: {} - Error: {}", q, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/owner")
    @Operation(summary = "Get owner's restaurants", description = "Retrieves all restaurants owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner's restaurants retrieved successfully",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<RestaurantResponse>> getMyRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Get owner's restaurants request received for user: {} - page: {}, size: {}", currentUserId, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<RestaurantResponse> response = restaurantService.getRestaurantsByOwner(currentUserId, pageable);
            log.info("Retrieved {} restaurants for owner: {}", response.getContent().size(), currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve restaurants for owner: {} - Error: {}", currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Update restaurant", description = "Updates a restaurant (owner or admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant updated successfully",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<RestaurantResponse> editRestaurant(@PathVariable UUID id,
                                                            @Valid @RequestBody RestaurantRequest request,
                                                            Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Restaurant update request received for ID: {} by user: {} with name: {}", id, currentUserId, request.getName());
        try {
            RestaurantResponse response = restaurantService.updateRestaurant(id, request, currentUserId);
            log.info("Restaurant updated successfully with ID: {} by user: {}", id, currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Restaurant update failed for ID: {} by user: {} - Error: {}", id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete restaurant", description = "Deletes a restaurant (owner or admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<Void> deleteRestaurant(@PathVariable UUID id, Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Restaurant deletion request received for ID: {} by user: {}", id, currentUserId);
        try {
            restaurantService.deleteRestaurant(id, currentUserId);
            log.info("Restaurant deleted successfully with ID: {} by user: {}", id, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Restaurant deletion failed for ID: {} by user: {} - Error: {}", id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }
}
