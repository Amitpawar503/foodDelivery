package com.food.delivery.service.impl;

import com.food.delivery.dto.MealRequest;
import com.food.delivery.dto.MealResponse;
import com.food.delivery.entity.Meal;
import com.food.delivery.entity.Restaurant;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.MealRepository;
import com.food.delivery.repository.spec.MealSpecification;
import com.food.delivery.service.MealService;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class MealServiceImpl implements MealService {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    @Override
    public MealResponse createMeal(UUID restaurantId, MealRequest request, UUID currentUserId) {
        Restaurant restaurant = restaurantService.getRestaurantEntityById(restaurantId);
        User currentUser = userService.getUserEntityById(currentUserId);

        // Check if user is owner or admin
        if (currentUser.getRole() != UserRole.ADMIN && 
            !restaurant.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        Meal meal = new Meal();
        meal.setName(request.getName());
        meal.setDescription(request.getDescription());
        meal.setPrice(request.getPrice());
        meal.setRestaurant(restaurant);

        Meal savedMeal = mealRepository.save(meal);
        log.info("Created meal: {} in restaurant: {} by user: {}", 
                savedMeal.getName(), restaurant.getName(), currentUser.getEmail());

        return mapToMealResponse(savedMeal);
    }

    @Override
    public MealResponse getMealById(UUID mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.MEAL_NOT_FOUND_MESSAGE + ": " + mealId));

        return mapToMealResponse(meal);
    }

    @Override
    public Page<MealResponse> getMealsByRestaurant(UUID restaurantId, Pageable pageable) {
        Page<Meal> meals = mealRepository.findByRestaurantId(restaurantId, pageable);
        return meals.map(this::mapToMealResponse);
    }

    @Override
    public List<MealResponse> getAllMealsByRestaurant(UUID restaurantId) {
        List<Meal> meals = mealRepository.findByRestaurantId(restaurantId);
        return meals.stream().map(this::mapToMealResponse).toList();
    }

    @Override
    public Page<MealResponse> searchMealsByRestaurant(UUID restaurantId, String search, Pageable pageable) {
        Page<Meal> meals = mealRepository.findAll(
            MealSpecification.hasRestaurantAndSearch(restaurantId, search), pageable);
        return meals.map(this::mapToMealResponse);
    }

    @Override
    public Page<MealResponse> getMealsByPriceRange(UUID restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Meal> meals = mealRepository.findAll(
            MealSpecification.hasRestaurantAndPriceRange(restaurantId, minPrice, maxPrice), pageable);
        return meals.map(this::mapToMealResponse);
    }

    @Override
    public MealResponse updateMeal(UUID mealId, MealRequest request, UUID currentUserId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.MEAL_NOT_FOUND_MESSAGE + ": " + mealId));

        User currentUser = userService.getUserEntityById(currentUserId);

        // Check if user is owner or admin
        if (currentUser.getRole() != UserRole.ADMIN && 
            !meal.getRestaurant().getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        if (request.getName() != null) {
            meal.setName(request.getName());
        }
        if (request.getDescription() != null) {
            meal.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            meal.setPrice(request.getPrice());
        }

        Meal updatedMeal = mealRepository.save(meal);
        log.info("Updated meal: {} by user: {}", updatedMeal.getName(), currentUser.getEmail());

        return mapToMealResponse(updatedMeal);
    }

    @Override
    public void deleteMeal(UUID mealId, UUID currentUserId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.MEAL_NOT_FOUND_MESSAGE + ": " + mealId));

        User currentUser = userService.getUserEntityById(currentUserId);

        // Check if user is owner or admin
        if (currentUser.getRole() != UserRole.ADMIN && 
            !meal.getRestaurant().getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        mealRepository.delete(meal);
        log.info("Deleted meal: {} by user: {}", meal.getName(), currentUser.getEmail());
    }

    private MealResponse mapToMealResponse(Meal meal) {
        return MealResponse.builder()
                .id(meal.getId())
                .name(meal.getName())
                .description(meal.getDescription())
                .price(meal.getPrice())
                .restaurantId(meal.getRestaurant().getId())
                .restaurantName(meal.getRestaurant().getName())
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .build();
    }
}
