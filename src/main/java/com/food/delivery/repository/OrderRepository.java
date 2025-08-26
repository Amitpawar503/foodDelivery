package com.food.delivery.repository;

import com.food.delivery.entity.Order;
import com.food.delivery.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    // Basic finder methods using Spring Data JPA naming conventions
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    
    Page<Order> findByRestaurantId(UUID restaurantId, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    List<Order> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status);
    
    // Custom finder methods for common queries
    Page<Order> findByCustomerIdAndStatus(UUID customerId, OrderStatus status, Pageable pageable);
    
    Page<Order> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable);
    
    Page<Order> findByOrderDateBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    
    Page<Order> findByCustomerIdAndOrderDateBetween(UUID customerId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    
    Page<Order> findByRestaurantIdAndOrderDateBetween(UUID restaurantId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    
    // Owner-scoped queries across their restaurants
    Page<Order> findByRestaurantOwnerId(UUID ownerId, Pageable pageable);
    
    Page<Order> findByRestaurantOwnerIdAndStatus(UUID ownerId, OrderStatus status, Pageable pageable);
    
    Page<Order> findByRestaurantOwnerIdAndOrderDateBetween(UUID ownerId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    
    // Additional utility methods
    List<Order> findByCustomerId(UUID customerId);
    
    List<Order> findByRestaurantId(UUID restaurantId);
    
    List<Order> findByStatus(OrderStatus status);
    
    long countByCustomerId(UUID customerId);
    
    long countByRestaurantId(UUID restaurantId);
    
    long countByStatus(OrderStatus status);
    
    long countByRestaurantOwnerId(UUID ownerId);
}
