package com.food.delivery.repository.spec;

import com.food.delivery.entity.Restaurant;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantSpecification {

    public static Specification<Restaurant> isBlocked(Boolean blocked) {
        return (root, query, criteriaBuilder) -> {
            if (blocked == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("blocked"), blocked);
        };
    }

    public static Specification<Restaurant> hasOwner(UUID ownerId) {
        return (root, query, criteriaBuilder) -> {
            if (ownerId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
        };
    }

    public static Specification<Restaurant> searchByNameOrDescription(String searchTerm) {
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

    public static Specification<Restaurant> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("blocked"), false);
    }

    public static Specification<Restaurant> hasOwnerAndBlocked(UUID ownerId, Boolean blocked) {
        return Specification.where(hasOwner(ownerId)).and(isBlocked(blocked));
    }


    public static Specification<Restaurant> searchActiveRestaurants(String searchTerm) {
        return Specification.where(isActive()).and(searchByNameOrDescription(searchTerm));
    }
}
