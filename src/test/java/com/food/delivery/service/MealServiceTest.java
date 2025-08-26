package com.food.delivery.service;

import com.food.delivery.dto.MealRequest;
import com.food.delivery.dto.MealResponse;
import com.food.delivery.entity.Meal;
import com.food.delivery.entity.Restaurant;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.MealRepository;
import com.food.delivery.service.impl.MealServiceImpl;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MealServiceImpl mealService;

    private User owner;
    private Restaurant restaurant;
    private Meal meal;
    private UUID ownerId;
    private UUID restaurantId;
    private UUID mealId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        mealId = UUID.randomUUID();

        owner = new User();
        owner.setId(ownerId);
        owner.setEmail("owner@example.com");
        owner.setName("Restaurant Owner");
        owner.setRole(UserRole.OWNER);

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");
        restaurant.setOwner(owner);

        meal = new Meal();
        meal.setId(mealId);
        meal.setName("Test Meal");
        meal.setDescription("A delicious test meal");
        meal.setPrice(BigDecimal.valueOf(15.99));
        meal.setRestaurant(restaurant);
    }

    @Test
    void createMeal_ValidRequest_ReturnsMealResponse() {
        // Arrange
        MealRequest request = new MealRequest();
        request.setName("New Meal");
        request.setDescription("A new delicious meal");
        request.setPrice(BigDecimal.valueOf(20.00));

        when(userService.getUserEntityById(ownerId)).thenReturn(owner);
        when(restaurantService.getRestaurantEntityById(restaurantId)).thenReturn(restaurant);
        when(mealRepository.save(any(Meal.class))).thenReturn(meal);

        // Act
        MealResponse result = mealService.createMeal(restaurantId, request, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(mealId, result.getId());
        assertEquals("Test Meal", result.getName());
        assertEquals(BigDecimal.valueOf(15.99), result.getPrice());
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    void createMeal_UnauthorizedUser_ThrowsException() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(UUID.randomUUID());
        unauthorizedUser.setRole(UserRole.CUSTOMER);

        MealRequest request = new MealRequest();
        request.setName("New Meal");

        when(userService.getUserEntityById(unauthorizedUser.getId())).thenReturn(unauthorizedUser);
        when(restaurantService.getRestaurantEntityById(restaurantId)).thenReturn(restaurant);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mealService.createMeal(restaurantId, request, unauthorizedUser.getId()));
        assertEquals(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE, exception.getMessage());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void getMealById_ExistingMeal_ReturnsMealResponse() {
        // Arrange
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));

        // Act
        MealResponse result = mealService.getMealById(mealId);

        // Assert
        assertNotNull(result);
        assertEquals(mealId, result.getId());
        assertEquals("Test Meal", result.getName());
        assertEquals(BigDecimal.valueOf(15.99), result.getPrice());
        verify(mealRepository).findById(mealId);
    }

    @Test
    void getMealById_NonExistingMeal_ThrowsException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(mealRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mealService.getMealById(nonExistingId));
        assertEquals(ErrorConstants.MEAL_NOT_FOUND_MESSAGE + ": " + nonExistingId, exception.getMessage());
        verify(mealRepository).findById(nonExistingId);
    }

    @Test
    void getAllMealsByRestaurant_ReturnsPageOfMeals() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Meal> meals = List.of(meal);
        Page<Meal> mealPage = new PageImpl<>(meals, pageable, meals.size());

        when(mealRepository.findByRestaurantId(restaurantId, pageable)).thenReturn(mealPage);

        // Act
        Page<MealResponse> result = mealService.getMealsByRestaurant(restaurantId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mealId, result.getContent().get(0).getId());
        verify(mealRepository).findByRestaurantId(restaurantId, pageable);
    }

    @Test
    void searchMealsByRestaurant_WithSearchTerm_ReturnsFilteredResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Meal> meals = List.of(meal);
        Page<Meal> mealPage = new PageImpl<>(meals, pageable, meals.size());

        when(mealRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mealPage);

        // Act
        Page<MealResponse> result = mealService.searchMealsByRestaurant(restaurantId, "test", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mealId, result.getContent().get(0).getId());
        verify(mealRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getMealsByPriceRange_ValidRange_ReturnsFilteredResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Meal> meals = List.of(meal);
        Page<Meal> mealPage = new PageImpl<>(meals, pageable, meals.size());

        when(mealRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mealPage);

        // Act
        Page<MealResponse> result = mealService.getMealsByPriceRange(restaurantId, BigDecimal.valueOf(10.00), BigDecimal.valueOf(20.00), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mealId, result.getContent().get(0).getId());
        verify(mealRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void updateMeal_ValidRequest_ReturnsUpdatedMeal() {
        // Arrange
        MealRequest request = new MealRequest();
        request.setName("Updated Meal");
        request.setDescription("Updated description");
        request.setPrice(BigDecimal.valueOf(25.00));

        Meal updatedMeal = new Meal();
        updatedMeal.setId(mealId);
        updatedMeal.setName("Updated Meal");
        updatedMeal.setDescription("Updated description");
        updatedMeal.setPrice(BigDecimal.valueOf(25.00));
        updatedMeal.setRestaurant(restaurant);

        when(userService.getUserEntityById(ownerId)).thenReturn(owner);
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        when(mealRepository.save(any(Meal.class))).thenReturn(updatedMeal);

        // Act
        MealResponse result = mealService.updateMeal(mealId, request, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(mealId, result.getId());
        assertEquals("Updated Meal", result.getName());
        assertEquals(BigDecimal.valueOf(25.00), result.getPrice());
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    void updateMeal_UnauthorizedUser_ThrowsException() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(UUID.randomUUID());
        unauthorizedUser.setRole(UserRole.CUSTOMER);

        MealRequest request = new MealRequest();
        request.setName("Updated Meal");

        when(userService.getUserEntityById(unauthorizedUser.getId())).thenReturn(unauthorizedUser);
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mealService.updateMeal(mealId, request, unauthorizedUser.getId()));
        assertEquals(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE, exception.getMessage());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void deleteMeal_ValidRequest_DeletesMeal() {
        // Arrange
        when(userService.getUserEntityById(ownerId)).thenReturn(owner);
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        doNothing().when(mealRepository).deleteById(mealId);

        // Act
        mealService.deleteMeal(mealId, ownerId);

        // Assert
        verify(mealRepository).findById(mealId);
        verify(mealRepository).deleteById(mealId);
    }

    @Test
    void deleteMeal_UnauthorizedUser_ThrowsException() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(UUID.randomUUID());
        unauthorizedUser.setRole(UserRole.CUSTOMER);

        when(userService.getUserEntityById(unauthorizedUser.getId())).thenReturn(unauthorizedUser);
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mealService.deleteMeal(mealId, unauthorizedUser.getId()));
        assertEquals(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE, exception.getMessage());
        verify(mealRepository, never()).deleteById(any(UUID.class));
    }
}
