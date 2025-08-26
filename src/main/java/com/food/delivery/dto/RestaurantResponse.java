package com.food.delivery.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class RestaurantResponse {
    UUID id;
    String name;
    String description;
    Boolean blocked;
    UUID ownerId;
    String ownerName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
