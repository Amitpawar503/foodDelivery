package com.food.delivery.repository.spec;

import com.food.delivery.entity.Meal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MealSpecification {

    /**
     * Specification to find meals by restaurant ID
     */
    public static Specification<Meal> hasRestaurant(UUID restaurantId) {
        return (root, query, criteriaBuilder) -> {
            if (restaurantId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId);
        };
    }

    /**
     * Specification to find meals within a price range
     */
    public static Specification<Meal> isInPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
        };
    }

    /**
     * Specification to search meals by name or description
     */
    public static Specification<Meal> searchByNameOrDescription(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }

    /**
     * Specification to find meals by restaurant and price range
     */
    public static Specification<Meal> hasRestaurantAndPriceRange(UUID restaurantId, BigDecimal minPrice, BigDecimal maxPrice) {
        return Specification.where(hasRestaurant(restaurantId)).and(isInPriceRange(minPrice, maxPrice));
    }

    /**
     * Specification to search meals by restaurant and name/description
     */
    public static Specification<Meal> hasRestaurantAndSearch(UUID restaurantId, String searchTerm) {
        return Specification.where(hasRestaurant(restaurantId)).and(searchByNameOrDescription(searchTerm));
    }

    /**
     * Specification to find meals by restaurant, price range, and search term
     */
    public static Specification<Meal> hasRestaurantAndPriceRangeAndSearch(UUID restaurantId, BigDecimal minPrice, BigDecimal maxPrice, String searchTerm) {
        return Specification.where(hasRestaurant(restaurantId))
                .and(isInPriceRange(minPrice, maxPrice))
                .and(searchByNameOrDescription(searchTerm));
    }
}
