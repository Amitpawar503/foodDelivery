package com.food.delivery.util;

import com.food.delivery.entity.User;
import com.food.delivery.entity.Restaurant;
import com.food.delivery.entity.Meal;
import com.food.delivery.entity.Order;
import com.food.delivery.enums.UserRole;

import java.math.BigDecimal;
import java.util.UUID;

public final class ValidationUtil {

    private ValidationUtil() {
        // Utility class - prevent instantiation
    }

    public static void validateUserAccess(User currentUser, User targetUser, String operation) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required for " + operation);
        }
        if (targetUser == null) {
            throw new IllegalArgumentException("Target user not found");
        }
    }

    public static void validateOwnerAccess(User currentUser, Restaurant restaurant, String operation) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required for " + operation);
        }
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        if (currentUser.getRole() != UserRole.ADMIN && 
            !restaurant.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only restaurant owner or admin can " + operation);
        }
    }

    public static void validateMealAccess(User currentUser, Meal meal, String operation) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required for " + operation);
        }
        if (meal == null) {
            throw new IllegalArgumentException("Meal not found");
        }
        if (currentUser.getRole() != UserRole.ADMIN && 
            !meal.getRestaurant().getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only restaurant owner or admin can " + operation);
        }
    }

    public static void validateOrderAccess(User currentUser, Order order, String operation) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required for " + operation);
        }
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        
        boolean canAccess = false;
        if (currentUser.getRole() == UserRole.ADMIN) {
            canAccess = true;
        } else if (currentUser.getRole() == UserRole.CUSTOMER && 
                   order.getCustomer().getId().equals(currentUser.getId())) {
            canAccess = true;
        } else if (currentUser.getRole() == UserRole.OWNER && 
                   order.getRestaurant().getOwner().getId().equals(currentUser.getId())) {
            canAccess = true;
        }
        
        if (!canAccess) {
            throw new SecurityException("Not authorized to " + operation + " this order");
        }
    }

    public static void validateAdminAccess(User currentUser, String operation) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required for " + operation);
        }
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new SecurityException("Admin role required for " + operation);
        }
    }

    public static void validatePrice(BigDecimal price, String fieldName) {
        if (price == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
        if (price.compareTo(BigDecimal.valueOf(10000)) > 0) {
            throw new IllegalArgumentException(fieldName + " cannot exceed 10000");
        }
    }

    public static void validateQuantity(Integer quantity, String fieldName) {
        if (quantity == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
        if (quantity > 100) {
            throw new IllegalArgumentException(fieldName + " cannot exceed 100");
        }
    }

    public static void validateDiscountPercent(Integer discountPercent) {
        if (discountPercent == null) {
            throw new IllegalArgumentException("Discount percent cannot be null");
        }
        if (discountPercent <= 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 1 and 100");
        }
    }

    public static UUID parseUUID(String uuidString, String fieldName) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " format: " + uuidString);
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (email.length() > 255) {
            throw new IllegalArgumentException("Email too long");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (password.length() > 100) {
            throw new IllegalArgumentException("Password too long");
        }
    }

    public static void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException(fieldName + " too long");
        }
    }
}
