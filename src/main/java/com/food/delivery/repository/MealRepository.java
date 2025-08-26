package com.food.delivery.repository;

import com.food.delivery.entity.Meal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID>, JpaSpecificationExecutor<Meal> {

    // Basic finder methods using Spring Data JPA naming conventions
    Page<Meal> findByRestaurantId(UUID restaurantId, Pageable pageable);
    
    List<Meal> findByRestaurantId(UUID restaurantId);
    
    // Custom finder methods for common queries
    Page<Meal> findByRestaurantIdAndPriceBetween(UUID restaurantId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<Meal> findByRestaurantIdAndNameContainingIgnoreCase(UUID restaurantId, String name, Pageable pageable);
    
    Page<Meal> findByRestaurantIdAndDescriptionContainingIgnoreCase(UUID restaurantId, String description, Pageable pageable);
    
    Page<Meal> findByRestaurantIdAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            UUID restaurantId, String name, String description, Pageable pageable);
    
    // Price range queries
    Page<Meal> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<Meal> findByPriceGreaterThanEqual(BigDecimal minPrice, Pageable pageable);
    
    Page<Meal> findByPriceLessThanEqual(BigDecimal maxPrice, Pageable pageable);
    
    // Additional utility methods
    List<Meal> findByRestaurantIdAndPriceBetween(UUID restaurantId, BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Meal> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    long countByRestaurantId(UUID restaurantId);
    
    long countByRestaurantIdAndPriceBetween(UUID restaurantId, BigDecimal minPrice, BigDecimal maxPrice);
    
    boolean existsByRestaurantIdAndName(UUID restaurantId, String name);
}
