package com.food.delivery.repository.spec;

import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {


    public static Specification<User> hasRole(UserRole role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("role"), role);
        };
    }

    public static Specification<User> isBlocked(Boolean blocked) {
        return (root, query, criteriaBuilder) -> {
            if (blocked == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("blocked"), blocked);
        };
    }

    public static Specification<User> hasRoleAndBlocked(UserRole role, Boolean blocked) {
        return Specification.where(hasRole(role)).and(isBlocked(blocked));
    }

    public static Specification<User> searchByNameOrEmail(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern)
            );
        };
    }

    public static Specification<User> isActive() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("blocked"), false);
    }

    public static Specification<User> hasActiveRole(UserRole role) {
        return Specification.where(hasRole(role)).and(isActive());
    }
}
