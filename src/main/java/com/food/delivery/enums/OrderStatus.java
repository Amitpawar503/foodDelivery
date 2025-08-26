package com.food.delivery.enums;

/**
 * Order status values in the food delivery system
 */
public enum OrderStatus {
    /**
     * Order has been placed by customer
     */
    PLACED,
    
    /**
     * Order has been canceled
     */
    CANCELED,
    
    /**
     * Restaurant is processing the order
     */
    PROCESSING,
    
    /**
     * Order is in route for delivery
     */
    IN_ROUTE,
    
    /**
     * Order has been delivered
     */
    DELIVERED,
    
    /**
     * Order has been received by customer
     */
    RECEIVED
}
