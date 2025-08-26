package com.food.delivery.service.impl;

import com.food.delivery.dto.OrderRequest;
import com.food.delivery.dto.OrderResponse;
import com.food.delivery.dto.OrderStatusUpdateRequest;
import com.food.delivery.dto.OrderUpdateRequest;
import com.food.delivery.entity.*;
import com.food.delivery.enums.OrderStatus;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.*;
import com.food.delivery.repository.spec.OrderSpecification;
import com.food.delivery.service.OrderService;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRestaurantBlockRepository userRestaurantBlockRepository;

    @Override
    public OrderResponse placeOrder(OrderRequest request, UUID currentUserId) {
        User customer = userService.getUserEntityById(currentUserId);
        Restaurant restaurant = restaurantService.getRestaurantEntityById(request.getRestaurantId());

        if (Boolean.TRUE.equals(customer.getBlocked())) {
            throw new SecurityException(ErrorConstants.USER_BLOCKED_MESSAGE);
        }
        if (Boolean.TRUE.equals(restaurant.getBlocked())) {
            throw new IllegalArgumentException(ErrorConstants.RESTAURANT_BLOCKED_MESSAGE);
        }
        if (userRestaurantBlockRepository.existsByUserIdAndRestaurantId(customer.getId(), restaurant.getId())) {
            throw new SecurityException(ErrorConstants.USER_BLOCKED_MESSAGE);
        }

        // Validate items and ensure they all belong to the same restaurant
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException(ErrorConstants.EMPTY_ORDER_ITEMS_MESSAGE);
        }

        Map<UUID, Integer> mealIdToQty = new HashMap<>();
        for (OrderRequest.OrderItemRequest item : request.getItems()) {
            UUID mealId = item.getMealId();
            mealIdToQty.merge(mealId, item.getQuantity(), Integer::sum);
        }

        List<Meal> meals = mealRepository.findAllById(mealIdToQty.keySet());
        if (meals.size() != mealIdToQty.size()) {
            throw new IllegalArgumentException(ErrorConstants.MEAL_NOT_FOUND_MESSAGE);
        }
        for (Meal meal : meals) {
            if (!meal.getRestaurant().getId().equals(restaurant.getId())) {
                throw new IllegalArgumentException("All items must be from the same restaurant");
            }
        }

        BigDecimal itemsTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (Meal meal : meals) {
            int qty = mealIdToQty.get(meal.getId());
            BigDecimal line = meal.getPrice().multiply(BigDecimal.valueOf(qty));
            itemsTotal = itemsTotal.add(line);

            OrderItem oi = new OrderItem();
            oi.setMeal(meal);
            oi.setQuantity(qty);
            oi.setPriceAtOrder(meal.getPrice());
            orderItems.add(oi);
        }

        BigDecimal tip = request.getTipAmount() == null ? BigDecimal.ZERO : request.getTipAmount();
        BigDecimal discount = BigDecimal.ZERO;
        Coupon appliedCoupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            appliedCoupon = couponRepository.findByCode(request.getCouponCode()).orElse(null);
            if (appliedCoupon == null || !appliedCoupon.isValid()) {
                throw new IllegalArgumentException(ErrorConstants.COUPON_EXPIRED_MESSAGE);
            }
            discount = itemsTotal.multiply(BigDecimal.valueOf(appliedCoupon.getDiscountPercent()).divide(BigDecimal.valueOf(100)));
        }

        BigDecimal total = itemsTotal.subtract(discount).add(tip);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setOrderDate(LocalDateTime.now());
        order.setTipAmount(tip);
        order.setCoupon(appliedCoupon);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PLACED);

        Order saved = orderRepository.save(order);
        for (OrderItem oi : orderItems) {
            oi.setOrder(saved);
        }
        // Persist items
        // Use mealRepository save? We need OrderItemRepository; instead map through order.getOrderItems
        saved.setOrderItems(orderItems);
        Order savedWithItems = orderRepository.save(saved);

        log.info("Order placed: {} by {} at {}", savedWithItems.getId(), customer.getEmail(), restaurant.getName());
        return mapToOrderResponse(savedWithItems);
    }

    @Override
    public Page<OrderResponse> findOrdersForCurrentUser(Pageable pageable, UUID currentUserId, Optional<OrderStatus> status,
                                                       Optional<LocalDateTime> from, Optional<LocalDateTime> to) {
        User user = userService.getUserEntityById(currentUserId);
        if (user.getRole() == UserRole.CUSTOMER) {
            Page<Order> page;
            if (from.isPresent() && to.isPresent()) {
                page = orderRepository.findByCustomerIdAndOrderDateBetween(user.getId(), from.get(), to.get(), pageable);
            } else if (status.isPresent()) {
                page = orderRepository.findByCustomerIdAndStatus(user.getId(), status.get(), pageable);
            } else {
                page = orderRepository.findByCustomerId(user.getId(), pageable);
            }
            return page.map(this::mapToOrderResponse);
        }
        if (user.getRole() == UserRole.OWNER) {
            Page<Order> page;
            if (from.isPresent() && to.isPresent()) {
                page = orderRepository.findByRestaurantOwnerIdAndOrderDateBetween(user.getId(), from.get(), to.get(), pageable);
            } else if (status.isPresent()) {
                page = orderRepository.findByRestaurantOwnerIdAndStatus(user.getId(), status.get(), pageable);
            } else {
                page = orderRepository.findByRestaurantOwnerId(user.getId(), pageable);
            }
            return page.map(this::mapToOrderResponse);
        }
        // ADMIN
        Page<Order> page;
        if (from.isPresent() && to.isPresent()) {
            page = orderRepository.findByOrderDateBetween(from.get(), to.get(), pageable);
        } else if (status.isPresent()) {
            page = orderRepository.findByStatus(status.get(), pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }
        return page.map(this::mapToOrderResponse);
    }

    @Override
    public OrderResponse getOrderById(UUID orderId, UUID currentUserId) {
        User user = userService.getUserEntityById(currentUserId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (user.getRole() == UserRole.CUSTOMER && !order.getCustomer().getId().equals(user.getId())) {
            throw new SecurityException("Not allowed");
        }
        if (user.getRole() == UserRole.OWNER && !order.getRestaurant().getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Not allowed");
        }
        return mapToOrderResponse(order);
    }

    @Override
    public OrderResponse updateStatus(UUID orderId, OrderStatusUpdateRequest req, UUID currentUserId) {
        User user = userService.getUserEntityById(currentUserId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus from = order.getStatus();
        OrderStatus to = req.getStatus();

        if (to == OrderStatus.CANCELED) {
            if (user.getRole() == UserRole.CUSTOMER && from == OrderStatus.PLACED) {
                order.setStatus(OrderStatus.CANCELED);
            } else if (user.getRole() == UserRole.OWNER && (from == OrderStatus.PLACED || from == OrderStatus.PROCESSING)) {
                order.setStatus(OrderStatus.CANCELED);
            } else if (user.getRole() == UserRole.ADMIN) {
                order.setStatus(OrderStatus.CANCELED);
            } else {
                throw new SecurityException("Cannot cancel at this stage");
            }
        } else {
            // forward-only progression
            List<OrderStatus> sequence = Arrays.asList(
                    OrderStatus.PLACED,
                    OrderStatus.PROCESSING,
                    OrderStatus.IN_ROUTE,
                    OrderStatus.DELIVERED,
                    OrderStatus.RECEIVED
            );
            int iFrom = sequence.indexOf(from);
            int iTo = sequence.indexOf(to);
            if (iTo <= iFrom) {
                throw new IllegalArgumentException("Status cannot move backward");
            }
            // Permission check
            if (user.getRole() == UserRole.OWNER) {
                // owner allowed to set PROCESSING, IN_ROUTE, DELIVERED
                if (!(to == OrderStatus.PROCESSING || to == OrderStatus.IN_ROUTE || to == OrderStatus.DELIVERED)) {
                    throw new SecurityException("Owner cannot set this status");
                }
                if (!order.getRestaurant().getOwner().getId().equals(user.getId())) {
                    throw new SecurityException("Not allowed");
                }
            } else if (user.getRole() == UserRole.CUSTOMER) {
                // customer only RECEIVED after DELIVERED
                if (!(to == OrderStatus.RECEIVED && from == OrderStatus.DELIVERED)) {
                    throw new SecurityException("Customer can only mark as RECEIVED after DELIVERED");
                }
                if (!order.getCustomer().getId().equals(user.getId())) {
                    throw new SecurityException("Not allowed");
                }
            }
            order.setStatus(to);
        }
        Order saved = orderRepository.save(order);
        return mapToOrderResponse(saved);
    }

    @Override
    public OrderResponse updateOrder(UUID orderId, OrderUpdateRequest req, UUID currentUserId) {
        User user = userService.getUserEntityById(currentUserId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getCustomer().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new SecurityException("Only customer or admin can update order");
        }
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalArgumentException("Only orders in PLACED status can be updated");
        }

        BigDecimal tip = req.getTipAmount() != null ? req.getTipAmount() : order.getTipAmount();
        Coupon coupon = order.getCoupon();
        if (req.getCouponCode() != null) {
            if (req.getCouponCode().isBlank()) {
                coupon = null;
            } else {
                Coupon c = couponRepository.findByCode(req.getCouponCode()).orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
                if (!c.isValid()) {
                    throw new IllegalArgumentException("Invalid or expired coupon");
                }
                coupon = c;
            }
        }

        BigDecimal itemsTotal = order.getOrderItems().stream()
                .map(oi -> oi.getPriceAtOrder().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = coupon == null ? BigDecimal.ZERO : itemsTotal.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()).divide(BigDecimal.valueOf(100)));
        BigDecimal total = itemsTotal.subtract(discount).add(tip);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        order.setTipAmount(tip);
        order.setCoupon(coupon);
        order.setTotalAmount(total);

        return mapToOrderResponse(orderRepository.save(order));
    }

    @Override
    public void cancelOrder(UUID orderId, UUID currentUserId) {
        updateStatus(orderId, wrapStatus(OrderStatus.CANCELED), currentUserId);
    }

    private OrderStatusUpdateRequest wrapStatus(OrderStatus s) {
        OrderStatusUpdateRequest r = new OrderStatusUpdateRequest();
        r.setStatus(s);
        return r;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .tipAmount(order.getTipAmount())
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .discountPercent(order.getCoupon() != null ? order.getCoupon().getDiscountPercent() : null)
                .status(order.getStatus())
                .items(order.getOrderItems() == null ? List.of() : order.getOrderItems().stream().map(oi -> 
                    OrderResponse.OrderItemResponse.builder()
                        .mealId(oi.getMeal().getId())
                        .mealName(oi.getMeal().getName())
                        .quantity(oi.getQuantity())
                        .priceAtOrder(oi.getPriceAtOrder())
                        .build()
                ).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
