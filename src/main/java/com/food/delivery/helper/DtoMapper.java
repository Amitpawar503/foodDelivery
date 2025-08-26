package com.food.delivery.helper;

import com.food.delivery.dto.*;
import com.food.delivery.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public final class DtoMapper {

    private DtoMapper() {
        // Helper class - prevent instantiation
    }

    // User mapping
    public static UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .blocked(user.getBlocked())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Restaurant mapping
    public static RestaurantResponse mapToRestaurantResponse(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }
        
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .blocked(restaurant.getBlocked())
                .ownerId(restaurant.getOwner() != null ? restaurant.getOwner().getId() : null)
                .ownerName(restaurant.getOwner() != null ? restaurant.getOwner().getName() : null)
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }

    // Meal mapping
    public static MealResponse mapToMealResponse(Meal meal) {
        if (meal == null) {
            return null;
        }
        
        return MealResponse.builder()
                .id(meal.getId())
                .name(meal.getName())
                .description(meal.getDescription())
                .price(meal.getPrice())
                .restaurantId(meal.getRestaurant() != null ? meal.getRestaurant().getId() : null)
                .restaurantName(meal.getRestaurant() != null ? meal.getRestaurant().getName() : null)
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .build();
    }

    // Order mapping
    public static OrderResponse mapToOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        
        List<OrderResponse.OrderItemResponse> itemResponses = null;
        if (order.getOrderItems() != null) {
            itemResponses = order.getOrderItems().stream()
                    .map(DtoMapper::mapToOrderItemResponse)
                    .collect(Collectors.toList());
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getName() : null)
                .restaurantId(order.getRestaurant() != null ? order.getRestaurant().getId() : null)
                .restaurantName(order.getRestaurant() != null ? order.getRestaurant().getName() : null)
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .tipAmount(order.getTipAmount())
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .discountPercent(order.getCoupon() != null ? order.getCoupon().getDiscountPercent() : null)
                .status(order.getStatus())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    // OrderItem mapping
    public static OrderResponse.OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        
        return OrderResponse.OrderItemResponse.builder()
                .mealId(orderItem.getMeal() != null ? orderItem.getMeal().getId() : null)
                .mealName(orderItem.getMeal() != null ? orderItem.getMeal().getName() : null)
                .quantity(orderItem.getQuantity())
                .priceAtOrder(orderItem.getPriceAtOrder())
                .build();
    }

    // Coupon mapping
    public static CouponResponse mapToCouponResponse(Coupon coupon) {
        if (coupon == null) {
            return null;
        }
        
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountPercent(coupon.getDiscountPercent())
                .expiresAt(coupon.getExpiresAt())
                .active(coupon.getActive())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }

    // List mapping helpers
    public static List<UserResponse> mapToUserResponseList(List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(DtoMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public static List<RestaurantResponse> mapToRestaurantResponseList(List<Restaurant> restaurants) {
        if (restaurants == null) {
            return List.of();
        }
        return restaurants.stream()
                .map(DtoMapper::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    public static List<MealResponse> mapToMealResponseList(List<Meal> meals) {
        if (meals == null) {
            return List.of();
        }
        return meals.stream()
                .map(DtoMapper::mapToMealResponse)
                .collect(Collectors.toList());
    }

    public static List<OrderResponse> mapToOrderResponseList(List<Order> orders) {
        if (orders == null) {
            return List.of();
        }
        return orders.stream()
                .map(DtoMapper::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public static List<CouponResponse> mapToCouponResponseList(List<Coupon> coupons) {
        if (coupons == null) {
            return List.of();
        }
        return coupons.stream()
                .map(DtoMapper::mapToCouponResponse)
                .collect(Collectors.toList());
    }
}
