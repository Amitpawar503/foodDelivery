package com.food.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderRequest {

    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    @NotEmpty(message = "Order items are required")
    @Size(max = 20, message = "Maximum 20 items allowed per order")
    private List<@Valid OrderItemRequest> items;

    @DecimalMin(value = "0.00", message = "Tip amount cannot be negative")
    private BigDecimal tipAmount;

    private String couponCode;

    @Data
    @Builder
    public static class OrderItemRequest {
        @NotNull(message = "Meal ID is required")
        private UUID mealId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}
