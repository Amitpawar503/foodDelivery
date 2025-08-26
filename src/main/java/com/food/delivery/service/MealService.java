package com.food.delivery.service;

import com.food.delivery.dto.MealRequest;
import com.food.delivery.dto.MealResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface MealService {

    MealResponse createMeal(UUID restaurantId, MealRequest request, UUID currentUserId);


    MealResponse getMealById(UUID mealId);


    Page<MealResponse> getMealsByRestaurant(UUID restaurantId, Pageable pageable);


    java.util.List<MealResponse> getAllMealsByRestaurant(UUID restaurantId);

    Page<MealResponse> searchMealsByRestaurant(UUID restaurantId, String search, Pageable pageable);


    Page<MealResponse> getMealsByPriceRange(UUID restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);


    MealResponse updateMeal(UUID mealId, MealRequest request, UUID currentUserId);


    void deleteMeal(UUID mealId, UUID currentUserId);
}
