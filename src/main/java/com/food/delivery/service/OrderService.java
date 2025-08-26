package com.food.delivery.service;

import com.food.delivery.dto.OrderRequest;
import com.food.delivery.dto.OrderResponse;
import com.food.delivery.dto.OrderStatusUpdateRequest;
import com.food.delivery.dto.OrderUpdateRequest;
import com.food.delivery.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {


    OrderResponse placeOrder(OrderRequest request, UUID currentUserId);


    Page<OrderResponse> findOrdersForCurrentUser(Pageable pageable, UUID currentUserId, 
                                                Optional<OrderStatus> status,
                                                Optional<LocalDateTime> from, Optional<LocalDateTime> to);


    OrderResponse getOrderById(UUID orderId, UUID currentUserId);


    OrderResponse updateStatus(UUID orderId, OrderStatusUpdateRequest req, UUID currentUserId);


    OrderResponse updateOrder(UUID orderId, OrderUpdateRequest req, UUID currentUserId);


    void cancelOrder(UUID orderId, UUID currentUserId);
}
