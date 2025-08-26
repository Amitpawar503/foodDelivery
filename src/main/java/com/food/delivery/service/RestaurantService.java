package com.food.delivery.service;

import com.food.delivery.dto.RestaurantRequest;
import com.food.delivery.dto.RestaurantResponse;
import com.food.delivery.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RestaurantService {


    RestaurantResponse createRestaurant(RestaurantRequest request, UUID ownerId);


    RestaurantResponse getRestaurantById(UUID restaurantId);


    Restaurant getRestaurantEntityById(UUID restaurantId);


    Page<RestaurantResponse> getAllRestaurants(Pageable pageable);


    Page<RestaurantResponse> searchRestaurants(String searchTerm, Pageable pageable);


    Page<RestaurantResponse> getRestaurantsByOwner(UUID ownerId, Pageable pageable);


    RestaurantResponse updateRestaurant(UUID restaurantId, RestaurantRequest request, UUID currentUserId);


    void deleteRestaurant(UUID restaurantId, UUID currentUserId);


    void blockRestaurant(UUID restaurantId, UUID adminId);

    void unblockRestaurant(UUID restaurantId, UUID adminId);
}
