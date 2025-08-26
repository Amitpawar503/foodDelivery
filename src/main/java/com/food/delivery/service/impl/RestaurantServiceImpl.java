package com.food.delivery.service.impl;

import com.food.delivery.dto.RestaurantRequest;
import com.food.delivery.dto.RestaurantResponse;
import com.food.delivery.entity.Restaurant;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.RestaurantRepository;
import com.food.delivery.repository.spec.RestaurantSpecification;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserService userService;

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request, UUID ownerId) {
        User owner = userService.getUserEntityById(ownerId);
        
        if (owner.getRole() != UserRole.OWNER && owner.getRole() != UserRole.ADMIN) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setOwner(owner);
        restaurant.setBlocked(false);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Created restaurant: {} by owner: {}", savedRestaurant.getName(), owner.getEmail());
        
        return mapToRestaurantResponse(savedRestaurant);
    }

    @Override
    public Restaurant getRestaurantEntityById(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.RESTAURANT_NOT_FOUND_MESSAGE + ": " + restaurantId));
    }

    @Override
    public RestaurantResponse getRestaurantById(UUID restaurantId) {
        Restaurant restaurant = getRestaurantEntityById(restaurantId);
        return mapToRestaurantResponse(restaurant);
    }

    @Override
    public Page<RestaurantResponse> getAllRestaurants(Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.findByBlockedFalse(pageable);
        return restaurants.map(this::mapToRestaurantResponse);
    }

    @Override
    public Page<RestaurantResponse> searchRestaurants(String searchTerm, Pageable pageable) {
        // Using specification for search functionality
        Page<Restaurant> restaurants = restaurantRepository.findAll(
            RestaurantSpecification.searchActiveRestaurants(searchTerm), pageable);
        return restaurants.map(this::mapToRestaurantResponse);
    }

    @Override
    public Page<RestaurantResponse> getRestaurantsByOwner(UUID ownerId, Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId, pageable);
        return restaurants.map(this::mapToRestaurantResponse);
    }

    @Override
    public RestaurantResponse updateRestaurant(UUID restaurantId, RestaurantRequest request, UUID currentUserId) {
        Restaurant restaurant = getRestaurantEntityById(restaurantId);

        User currentUser = userService.getUserEntityById(currentUserId);
        
        // Check if user is owner or admin
        if (currentUser.getRole() != UserRole.ADMIN && 
            !restaurant.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Updated restaurant: {} by user: {}", updatedRestaurant.getName(), currentUser.getEmail());
        
        return mapToRestaurantResponse(updatedRestaurant);
    }

    @Override
    public void deleteRestaurant(UUID restaurantId, UUID currentUserId) {
        Restaurant restaurant = getRestaurantEntityById(restaurantId);

        User currentUser = userService.getUserEntityById(currentUserId);
        
        // Check if user is owner or admin
        if (currentUser.getRole() != UserRole.ADMIN && 
            !restaurant.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        restaurantRepository.delete(restaurant);
        log.info("Deleted restaurant: {} by user: {}", restaurant.getName(), currentUser.getEmail());
    }

    @Override
    public void blockRestaurant(UUID restaurantId, UUID adminId) {
        Restaurant restaurant = getRestaurantEntityById(restaurantId);
        User admin = userService.getUserEntityById(adminId);
        
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        restaurant.setBlocked(true);
        restaurantRepository.save(restaurant);
        log.info("Blocked restaurant: {} by admin: {}", restaurant.getName(), admin.getEmail());
    }

    @Override
    public void unblockRestaurant(UUID restaurantId, UUID adminId) {
        Restaurant restaurant = getRestaurantEntityById(restaurantId);
        User admin = userService.getUserEntityById(adminId);
        
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        restaurant.setBlocked(false);
        restaurantRepository.save(restaurant);
        log.info("Unblocked restaurant: {} by admin: {}", restaurant.getName(), admin.getEmail());
    }

    private RestaurantResponse mapToRestaurantResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .ownerId(restaurant.getOwner().getId())
                .ownerName(restaurant.getOwner().getName())
                .blocked(restaurant.getBlocked())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }
}
