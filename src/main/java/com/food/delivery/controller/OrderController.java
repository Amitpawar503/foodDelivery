package com.food.delivery.controller;

import com.food.delivery.dto.OrderRequest;
import com.food.delivery.dto.OrderResponse;
import com.food.delivery.dto.OrderStatusUpdateRequest;
import com.food.delivery.dto.OrderUpdateRequest;
import com.food.delivery.service.OrderService;
import com.food.delivery.constants.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
                                                   Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Order placement request received for user: {} at restaurant: {} with {} items", 
                currentUserId, request.getRestaurantId(), request.getItems().size());
        try {
            OrderResponse response = orderService.placeOrder(request, currentUserId);
            log.info("Order placed successfully with ID: {} for user: {} at restaurant: {} - total: {}", 
                    response.getId(), currentUserId, request.getRestaurantId(), response.getTotalAmount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Order placement failed for user: {} at restaurant: {} - Error: {}", 
                    currentUserId, request.getRestaurantId(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Get orders request received for user: {} - page: {}, size: {}, status: {}, from: {}, to: {}", 
                currentUserId, page, size, status, from, to);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Optional<com.food.delivery.enums.OrderStatus> statusOpt = status != null ? 
                    Optional.of(com.food.delivery.enums.OrderStatus.valueOf(status)) : Optional.empty();
            Optional<LocalDateTime> fromOpt = Optional.ofNullable(from);
            Optional<LocalDateTime> toOpt = Optional.ofNullable(to);
            
            Page<OrderResponse> response = orderService.findOrdersForCurrentUser(pageable, currentUserId, statusOpt, fromOpt, toOpt);
            log.info("Retrieved {} orders for user: {}", response.getContent().size(), currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve orders for user: {} - Error: {}", currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id, Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Order retrieval request received for ID: {} by user: {}", id, currentUserId);
        try {
            OrderResponse response = orderService.getOrderById(id, currentUserId);
            log.info("Order retrieved successfully with ID: {} by user: {}", id, currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Order retrieval failed for ID: {} by user: {} - Error: {}", id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/edit/status/{id}")
    public ResponseEntity<OrderResponse> editOrderStatus(@PathVariable UUID id,
                                                       @Valid @RequestBody OrderStatusUpdateRequest request,
                                                       Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Order status update request received for ID: {} by user: {} - new status: {}", 
                id, currentUserId, request.getStatus());
        try {
            OrderResponse response = orderService.updateStatus(id, request, currentUserId);
            log.info("Order status updated successfully for ID: {} by user: {} - new status: {}", 
                    id, currentUserId, request.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Order status update failed for ID: {} by user: {} - Error: {}", 
                    id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<OrderResponse> editOrder(@PathVariable UUID id,
                                                  @Valid @RequestBody OrderUpdateRequest request,
                                                  Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Order update request received for ID: {} by user: {} - tip: {}, coupon: {}", 
                id, currentUserId, request.getTipAmount(), request.getCouponCode());
        try {
            OrderResponse response = orderService.updateOrder(id, request, currentUserId);
            log.info("Order updated successfully for ID: {} by user: {} - new total: {}", 
                    id, currentUserId, response.getTotalAmount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Order update failed for ID: {} by user: {} - Error: {}", 
                    id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id, Authentication authentication) {
        UUID currentUserId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Order cancellation request received for ID: {} by user: {}", id, currentUserId);
        try {
            orderService.cancelOrder(id, currentUserId);
            log.info("Order cancelled successfully for ID: {} by user: {}", id, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Order cancellation failed for ID: {} by user: {} - Error: {}", 
                    id, currentUserId, e.getMessage(), e);
            throw e;
        }
    }
}
