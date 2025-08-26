package com.food.delivery.util;

import com.food.delivery.entity.Order;
import com.food.delivery.entity.User;
import com.food.delivery.enums.OrderStatus;
import com.food.delivery.enums.UserRole;

import java.util.Arrays;
import java.util.List;

public final class OrderStatusUtil {

    private OrderStatusUtil() {
        // Utility class - prevent instantiation
    }

    private static final List<OrderStatus> FORWARD_SEQUENCE = Arrays.asList(
            OrderStatus.PLACED,
            OrderStatus.PROCESSING,
            OrderStatus.IN_ROUTE,
            OrderStatus.DELIVERED,
            OrderStatus.RECEIVED
    );

    public static boolean isValidTransition(OrderStatus from, OrderStatus to) {
        if (from == to) {
            return false; // No change
        }

        if (to == OrderStatus.CANCELED) {
            // Cancel can happen from PLACED or PROCESSING
            return from == OrderStatus.PLACED || from == OrderStatus.PROCESSING;
        }

        // Check forward progression
        int fromIndex = FORWARD_SEQUENCE.indexOf(from);
        int toIndex = FORWARD_SEQUENCE.indexOf(to);
        
        return fromIndex >= 0 && toIndex > fromIndex;
    }

    public static boolean canUserChangeStatus(User user, Order order, OrderStatus newStatus) {
        if (user == null || order == null) {
            return false;
        }

        OrderStatus currentStatus = order.getStatus();

        // Admin can change to any status
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // Customer can only mark as RECEIVED after DELIVERED
        if (user.getRole() == UserRole.CUSTOMER) {
            return newStatus == OrderStatus.RECEIVED && 
                   currentStatus == OrderStatus.DELIVERED;
        }

        // Owner can change status for their restaurant
        if (user.getRole() == UserRole.OWNER) {
            if (!order.getRestaurant().getOwner().getId().equals(user.getId())) {
                return false;
            }

            // Owner allowed transitions
            switch (currentStatus) {
                case PLACED:
                    return newStatus == OrderStatus.PROCESSING || 
                           newStatus == OrderStatus.CANCELED;
                case PROCESSING:
                    return newStatus == OrderStatus.IN_ROUTE || 
                           newStatus == OrderStatus.CANCELED;
                case IN_ROUTE:
                    return newStatus == OrderStatus.DELIVERED;
                default:
                    return false;
            }
        }

        return false;
    }

    public static String getStatusTransitionMessage(OrderStatus from, OrderStatus to) {
        if (from == to) {
            return "No status change";
        }

        switch (to) {
            case PROCESSING:
                return "Order is now being prepared";
            case IN_ROUTE:
                return "Order is on its way";
            case DELIVERED:
                return "Order has been delivered";
            case RECEIVED:
                return "Order has been received by customer";
            case CANCELED:
                return "Order has been canceled";
            default:
                return "Order status changed from " + from + " to " + to;
        }
    }

    public static boolean isFinalStatus(OrderStatus status) {
        return status == OrderStatus.RECEIVED || status == OrderStatus.CANCELED;
    }

    public static boolean isEditableStatus(OrderStatus status) {
        return status == OrderStatus.PLACED;
    }

    public static List<OrderStatus> getNextPossibleStatuses(OrderStatus currentStatus) {
        switch (currentStatus) {
            case PLACED:
                return Arrays.asList(OrderStatus.PROCESSING, OrderStatus.CANCELED);
            case PROCESSING:
                return Arrays.asList(OrderStatus.IN_ROUTE, OrderStatus.CANCELED);
            case IN_ROUTE:
                return List.of(OrderStatus.DELIVERED);
            case DELIVERED:
                return List.of(OrderStatus.RECEIVED);
            case RECEIVED:
            case CANCELED:
                return List.of(); // Final states
            default:
                return List.of();
        }
    }
}
