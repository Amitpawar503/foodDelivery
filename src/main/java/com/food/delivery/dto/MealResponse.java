package com.food.delivery.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class MealResponse {
    UUID id;
    String name;
    String description;
    BigDecimal price;
    UUID restaurantId;
    String restaurantName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
