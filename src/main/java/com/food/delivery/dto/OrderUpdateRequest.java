package com.food.delivery.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderUpdateRequest {
    @DecimalMin(value = "0.00", message = "Tip amount cannot be negative")
    private BigDecimal tipAmount;

    private String couponCode;
}
