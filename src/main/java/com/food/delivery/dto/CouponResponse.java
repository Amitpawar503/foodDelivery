package com.food.delivery.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class CouponResponse {
    UUID id;
    String code;
    Integer discountPercent;
    LocalDateTime expiresAt;
    Boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
