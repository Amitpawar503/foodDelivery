package com.food.delivery.enums;

/**
 * User roles in the food delivery system
 */
public enum UserRole {
    /**
     * Customer role - can place orders and view restaurants
     */
    CUSTOMER,
    
    /**
     * Restaurant owner role - can manage restaurants and meals
     */
    OWNER,
    
    /**
     * Administrator role - can manage all entities
     */
    ADMIN
}
