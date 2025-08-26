package com.food.delivery.dto;

import com.food.delivery.entity.Order;
import com.food.delivery.enums.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderResponse {
    UUID id;
    UUID customerId;
    String customerName;
    UUID restaurantId;
    String restaurantName;
    LocalDateTime orderDate;
    BigDecimal totalAmount;
    BigDecimal tipAmount;
    String couponCode;
    Integer discountPercent;
    OrderStatus status;
    List<OrderItemResponse> items;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Value
    @Builder
    public static class OrderItemResponse {
        UUID mealId;
        String mealName;
        Integer quantity;
        BigDecimal priceAtOrder;
    }
}
