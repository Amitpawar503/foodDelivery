package com.food.delivery.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {
    @NotBlank
    private String code;
    @Min(1)
    @Max(100)
    private Integer discountPercent;
    private LocalDateTime expiresAt;
    private Boolean active = true;
}
