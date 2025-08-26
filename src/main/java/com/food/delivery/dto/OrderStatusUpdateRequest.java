package com.food.delivery.dto;

import com.food.delivery.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    @NotNull
    private OrderStatus status;
}
