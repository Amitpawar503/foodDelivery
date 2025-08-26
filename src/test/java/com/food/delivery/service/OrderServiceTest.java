package com.food.delivery.service;

import com.food.delivery.dto.OrderRequest;
import com.food.delivery.dto.OrderResponse;
import com.food.delivery.dto.OrderStatusUpdateRequest;
import com.food.delivery.entity.*;
import com.food.delivery.enums.OrderStatus;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.*;
import com.food.delivery.service.impl.OrderServiceImpl;
import com.food.delivery.constants.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private UserService userService;

    @Mock
    private UserRestaurantBlockRepository userRestaurantBlockRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User customer;
    private User owner;
    private Restaurant restaurant;
    private Meal meal;
    private Order order;
    private UUID customerId;
    private UUID restaurantId;
    private UUID mealId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        mealId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        customer = new User();
        customer.setId(customerId);
        customer.setEmail("customer@example.com");
        customer.setName("Customer");
        customer.setRole(UserRole.CUSTOMER);
        customer.setBlocked(false);

        owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setEmail("owner@example.com");
        owner.setName("Owner");
        owner.setRole(UserRole.OWNER);

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");
        restaurant.setOwner(owner);
        restaurant.setBlocked(false);

        meal = new Meal();
        meal.setId(mealId);
        meal.setName("Test Meal");
        meal.setPrice(BigDecimal.valueOf(10.00));
        meal.setRestaurant(restaurant);

        order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.valueOf(10.00));
    }

    @Test
    void placeOrder_ValidRequest_ReturnsOrderResponse() {
        // Arrange
        OrderRequest request = OrderRequest.builder()
                .restaurantId(restaurantId)
                .items(List.of(OrderRequest.OrderItemRequest.builder()
                        .mealId(mealId)
                        .quantity(2)
                        .build()))
                .tipAmount(BigDecimal.valueOf(2.00))
                .build();

        when(userService.getUserEntityById(customerId)).thenReturn(customer);
        when(restaurantService.getRestaurantEntityById(restaurantId)).thenReturn(restaurant);
        when(userRestaurantBlockRepository.existsByUserIdAndRestaurantId(customerId, restaurantId)).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponse result = orderService.placeOrder(request, customerId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(restaurantId, result.getRestaurantId());
        assertEquals(OrderStatus.PLACED, result.getStatus());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_BlockedCustomer_ThrowsException() {
        // Arrange
        customer.setBlocked(true);
        OrderRequest request = OrderRequest.builder()
                .restaurantId(restaurantId)
                .items(List.of(OrderRequest.OrderItemRequest.builder()
                        .mealId(mealId)
                        .quantity(1)
                        .build()))
                .build();

        when(userService.getUserEntityById(customerId)).thenReturn(customer);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
                () -> orderService.placeOrder(request, customerId));
        assertEquals(ErrorConstants.USER_BLOCKED_MESSAGE, exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_BlockedRestaurant_ThrowsException() {
        // Arrange
        restaurant.setBlocked(true);
        OrderRequest request = OrderRequest.builder()
                .restaurantId(restaurantId)
                .items(List.of(OrderRequest.OrderItemRequest.builder()
                        .mealId(mealId)
                        .quantity(1)
                        .build()))
                .build();

        when(userService.getUserEntityById(customerId)).thenReturn(customer);
        when(restaurantService.getRestaurantEntityById(restaurantId)).thenReturn(restaurant);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(request, customerId));
        assertEquals(ErrorConstants.RESTAURANT_BLOCKED_MESSAGE, exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_UserBlockedByRestaurant_ThrowsException() {
        // Arrange
        OrderRequest request = OrderRequest.builder()
                .restaurantId(restaurantId)
                .items(List.of(OrderRequest.OrderItemRequest.builder()
                        .mealId(mealId)
                        .quantity(1)
                        .build()))
                .build();

        when(userService.getUserEntityById(customerId)).thenReturn(customer);
        when(restaurantService.getRestaurantEntityById(restaurantId)).thenReturn(restaurant);
        when(userRestaurantBlockRepository.existsByUserIdAndRestaurantId(customerId, restaurantId)).thenReturn(true);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
                () -> orderService.placeOrder(request, customerId));
        assertEquals(ErrorConstants.USER_BLOCKED_MESSAGE, exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_EmptyItems_ThrowsException() {
        // Arrange
        OrderRequest request = OrderRequest.builder()
                .restaurantId(restaurantId)
                .items(List.of())
                .build();

        when(userService.getUserEntityById(customerId)).thenReturn(customer);
        when(restaurantService.getRestaurantEntityById(restaurantId)).thenReturn(restaurant);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(request, customerId));
        assertEquals(ErrorConstants.EMPTY_ORDER_ITEMS_MESSAGE, exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void findOrdersForCurrentUser_Customer_ReturnsCustomerOrders() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(userService.getUserEntityById(customerId)).thenReturn(customer);
        when(orderRepository.findByCustomerId(customerId, pageable)).thenReturn(orderPage);

        // Act
        Page<OrderResponse> result = orderService.findOrdersForCurrentUser(pageable, customerId, Optional.empty(), Optional.empty(), Optional.empty());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(orderId, result.getContent().get(0).getId());
        verify(orderRepository).findByCustomerId(customerId, pageable);
    }

    @Test
    void findOrdersForCurrentUser_Owner_ReturnsOwnerOrders() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(userService.getUserEntityById(owner.getId())).thenReturn(owner);
        when(orderRepository.findByRestaurantOwnerId(owner.getId(), pageable)).thenReturn(orderPage);

        // Act
        Page<OrderResponse> result = orderService.findOrdersForCurrentUser(pageable, owner.getId(), Optional.empty(), Optional.empty(), Optional.empty());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(orderId, result.getContent().get(0).getId());
        verify(orderRepository).findByRestaurantOwnerId(owner.getId(), pageable);
    }

    @Test
    void updateStatus_ValidTransition_UpdatesStatus() {
        // Arrange
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(OrderStatus.PROCESSING);

        when(userService.getUserEntityById(owner.getId())).thenReturn(owner);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponse result = orderService.updateStatus(orderId, request, owner.getId());

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateStatus_InvalidTransition_ThrowsException() {
        // Arrange
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(OrderStatus.DELIVERED);

        when(userService.getUserEntityById(customerId)).thenReturn(customer);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
                () -> orderService.updateStatus(orderId, request, customerId));
        assertEquals(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE, exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateStatus_OrderNotFound_ThrowsException() {
        // Arrange
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(OrderStatus.PROCESSING);

        when(userService.getUserEntityById(owner.getId())).thenReturn(owner);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.updateStatus(orderId, request, owner.getId()));
        assertEquals(ErrorConstants.ORDER_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_ValidRequest_CancelsOrder() {
        // Arrange
        when(userService.getUserEntityById(customerId)).thenReturn(customer);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.cancelOrder(orderId, customerId);

        // Assert
        verify(orderRepository).save(any(Order.class));
    }
}
