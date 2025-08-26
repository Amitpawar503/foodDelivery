package com.food.delivery.repository.spec;

import com.food.delivery.entity.Order;
import com.food.delivery.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderSpecification {


    public static Specification<Order> hasCustomer(UUID customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("customer").get("id"), customerId);
        };
    }

    public static Specification<Order> hasRestaurant(UUID restaurantId) {
        return (root, query, criteriaBuilder) -> {
            if (restaurantId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId);
        };
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }


    public static Specification<Order> hasOwner(UUID ownerId) {
        return (root, query, criteriaBuilder) -> {
            if (ownerId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("restaurant").get("owner").get("id"), ownerId);
        };
    }

    public static Specification<Order> isBetweenDates(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return criteriaBuilder.conjunction();
            }

            if (fromDate != null && toDate != null) {
                return criteriaBuilder.between(root.get("orderDate"), fromDate, toDate);
            } else if (fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), fromDate);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), toDate);
            }
        };
    }


    public static Specification<Order> hasCustomerAndDateRange(UUID customerId, LocalDateTime fromDate, LocalDateTime toDate) {
        return Specification.where(hasCustomer(customerId)).and(isBetweenDates(fromDate, toDate));
    }


    public static Specification<Order> hasRestaurantAndDateRange(UUID restaurantId, LocalDateTime fromDate, LocalDateTime toDate) {
        return Specification.where(hasRestaurant(restaurantId)).and(isBetweenDates(fromDate, toDate));
    }

    public static Specification<Order> hasRestaurantAndStatus(UUID restaurantId, OrderStatus status) {
        return Specification.where(hasRestaurant(restaurantId)).and(hasStatus(status));
    }


    public static Specification<Order> hasOwnerAndStatus(UUID ownerId, OrderStatus status) {
        return Specification.where(hasOwner(ownerId)).and(hasStatus(status));
    }

    public static Specification<Order> hasOwnerAndDateRange(UUID ownerId, LocalDateTime fromDate, LocalDateTime toDate) {
        return Specification.where(hasOwner(ownerId)).and(isBetweenDates(fromDate, toDate));
    }

    public static Specification<Order> hasCustomerAndStatus(UUID customerId, OrderStatus status) {
        return Specification.where(hasCustomer(customerId)).and(hasStatus(status));
    }
}
