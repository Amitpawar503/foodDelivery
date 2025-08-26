package com.food.delivery.service;

import com.food.delivery.dto.RestaurantRequest;
import com.food.delivery.dto.RestaurantResponse;
import com.food.delivery.entity.Restaurant;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.RestaurantRepository;
import com.food.delivery.service.impl.RestaurantServiceImpl;
import org.springframework.data.jpa.domain.Specification;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private User ownerUser;
    private User adminUser;
    private User customerUser;
    private Restaurant testRestaurant;
    private UUID ownerId;
    private UUID adminId;
    private UUID customerId;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        ownerUser = new User();
        ownerUser.setId(ownerId);
        ownerUser.setEmail("owner@example.com");
        ownerUser.setName("Owner User");
        ownerUser.setRole(UserRole.OWNER);
        ownerUser.setBlocked(false);

        adminUser = new User();
        adminUser.setId(adminId);
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setBlocked(false);

        customerUser = new User();
        customerUser.setId(customerId);
        customerUser.setEmail("customer@example.com");
        customerUser.setName("Customer User");
        customerUser.setRole(UserRole.CUSTOMER);
        customerUser.setBlocked(false);

        testRestaurant = new Restaurant();
        testRestaurant.setId(restaurantId);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setDescription("A test restaurant");
        testRestaurant.setOwner(ownerUser);
        testRestaurant.setBlocked(false);
        testRestaurant.setCreatedAt(LocalDateTime.now());
        testRestaurant.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createRestaurant_OwnerUser_ReturnsRestaurantResponse() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("New Restaurant");
        request.setDescription("A new restaurant");

        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setId(UUID.randomUUID());
        newRestaurant.setName("New Restaurant");
        newRestaurant.setDescription("A new restaurant");
        newRestaurant.setOwner(ownerUser);
        newRestaurant.setBlocked(false);

        when(userService.getUserEntityById(ownerId)).thenReturn(ownerUser);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(newRestaurant);

        // Act
        RestaurantResponse result = restaurantService.createRestaurant(request, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals("New Restaurant", result.getName());
        assertEquals("A new restaurant", result.getDescription());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals("Owner User", result.getOwnerName());
        assertFalse(result.getBlocked());

        verify(userService).getUserEntityById(ownerId);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void createRestaurant_AdminUser_ReturnsRestaurantResponse() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Admin Restaurant");
        request.setDescription("A restaurant created by admin");

        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setId(UUID.randomUUID());
        newRestaurant.setName("Admin Restaurant");
        newRestaurant.setDescription("A restaurant created by admin");
        newRestaurant.setOwner(adminUser);
        newRestaurant.setBlocked(false);

        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(newRestaurant);

        // Act
        RestaurantResponse result = restaurantService.createRestaurant(request, adminId);

        // Assert
        assertNotNull(result);
        assertEquals("Admin Restaurant", result.getName());
        verify(userService).getUserEntityById(adminId);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void createRestaurant_CustomerUser_ThrowsException() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Customer Restaurant");
        request.setDescription("A restaurant created by customer");

        when(userService.getUserEntityById(customerId)).thenReturn(customerUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            restaurantService.createRestaurant(request, customerId));
        verify(userService).getUserEntityById(customerId);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void getRestaurantById_Exists_ReturnsRestaurantResponse() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));

        // Act
        RestaurantResponse result = restaurantService.getRestaurantById(restaurantId);

        // Assert
        assertNotNull(result);
        assertEquals(restaurantId, result.getId());
        assertEquals("Test Restaurant", result.getName());
        assertEquals("A test restaurant", result.getDescription());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals("Owner User", result.getOwnerName());
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void getRestaurantById_NotExists_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            restaurantService.getRestaurantById(nonExistentId));
        verify(restaurantRepository).findById(nonExistentId);
    }

    @Test
    void getAllRestaurants_ReturnsPageOfRestaurants() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(testRestaurant), pageable, 1);
        when(restaurantRepository.findByBlockedFalse(pageable)).thenReturn(restaurantPage);

        // Act
        Page<RestaurantResponse> result = restaurantService.getAllRestaurants(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(restaurantId, result.getContent().get(0).getId());
        verify(restaurantRepository).findByBlockedFalse(pageable);
    }

    @Test
    void searchRestaurants_WithSearchTerm_ReturnsFilteredResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(testRestaurant), pageable, 1);
        when(restaurantRepository.findAll((Specification<Restaurant>) any(), eq(pageable))).thenReturn(restaurantPage);

        // Act
        Page<RestaurantResponse> result = restaurantService.searchRestaurants("test", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(restaurantRepository).findAll((Specification<Restaurant>) any(), eq(pageable));
    }

    @Test
    void getRestaurantsByOwner_ReturnsOwnerRestaurants() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(testRestaurant), pageable, 1);
        when(restaurantRepository.findByOwnerId(ownerId, pageable)).thenReturn(restaurantPage);

        // Act
        Page<RestaurantResponse> result = restaurantService.getRestaurantsByOwner(ownerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(ownerId, result.getContent().get(0).getOwnerId());
        verify(restaurantRepository).findByOwnerId(ownerId, pageable);
    }

    @Test
    void updateRestaurant_OwnerUser_ReturnsUpdatedRestaurant() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Updated Restaurant");
        request.setDescription("Updated description");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(ownerId)).thenReturn(ownerUser);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        RestaurantResponse result = restaurantService.updateRestaurant(restaurantId, request, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Restaurant", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(ownerId);
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void updateRestaurant_AdminUser_ReturnsUpdatedRestaurant() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Admin Updated Restaurant");
        request.setDescription("Admin updated description");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        RestaurantResponse result = restaurantService.updateRestaurant(restaurantId, request, adminId);

        // Assert
        assertNotNull(result);
        assertEquals("Admin Updated Restaurant", result.getName());
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(adminId);
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void updateRestaurant_UnauthorizedUser_ThrowsException() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Unauthorized Update");
        UUID unauthorizedUserId = UUID.randomUUID();
        User unauthorizedUser = new User();
        unauthorizedUser.setId(unauthorizedUserId);
        unauthorizedUser.setRole(UserRole.CUSTOMER);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(unauthorizedUserId)).thenReturn(unauthorizedUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            restaurantService.updateRestaurant(restaurantId, request, unauthorizedUserId));
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(unauthorizedUserId);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void updateRestaurant_PartialUpdate_ReturnsUpdatedRestaurant() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Partial Update");
        // description is null

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(ownerId)).thenReturn(ownerUser);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        RestaurantResponse result = restaurantService.updateRestaurant(restaurantId, request, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals("Partial Update", result.getName());
        assertEquals("A test restaurant", result.getDescription()); // unchanged
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(ownerId);
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void deleteRestaurant_OwnerUser_DeletesSuccessfully() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(ownerId)).thenReturn(ownerUser);
        doNothing().when(restaurantRepository).delete(testRestaurant);

        // Act
        restaurantService.deleteRestaurant(restaurantId, ownerId);

        // Assert
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(ownerId);
        verify(restaurantRepository).delete(testRestaurant);
    }

    @Test
    void deleteRestaurant_AdminUser_DeletesSuccessfully() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        doNothing().when(restaurantRepository).delete(testRestaurant);

        // Act
        restaurantService.deleteRestaurant(restaurantId, adminId);

        // Assert
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(adminId);
        verify(restaurantRepository).delete(testRestaurant);
    }

    @Test
    void deleteRestaurant_UnauthorizedUser_ThrowsException() {
        // Arrange
        UUID unauthorizedUserId = UUID.randomUUID();
        User unauthorizedUser = new User();
        unauthorizedUser.setId(unauthorizedUserId);
        unauthorizedUser.setRole(UserRole.CUSTOMER);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(unauthorizedUserId)).thenReturn(unauthorizedUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            restaurantService.deleteRestaurant(restaurantId, unauthorizedUserId));
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(unauthorizedUserId);
        verify(restaurantRepository, never()).delete(any(Restaurant.class));
    }

    @Test
    void blockRestaurant_AdminUser_BlocksSuccessfully() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        restaurantService.blockRestaurant(restaurantId, adminId);

        // Assert
        assertTrue(testRestaurant.getBlocked());
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(adminId);
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void blockRestaurant_NonAdminUser_ThrowsException() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(ownerId)).thenReturn(ownerUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            restaurantService.blockRestaurant(restaurantId, ownerId));
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(ownerId);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void unblockRestaurant_AdminUser_UnblocksSuccessfully() {
        // Arrange
        testRestaurant.setBlocked(true);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        restaurantService.unblockRestaurant(restaurantId, adminId);

        // Assert
        assertFalse(testRestaurant.getBlocked());
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(adminId);
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void unblockRestaurant_NonAdminUser_ThrowsException() {
        // Arrange
        testRestaurant.setBlocked(true);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(userService.getUserEntityById(ownerId)).thenReturn(ownerUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            restaurantService.unblockRestaurant(restaurantId, ownerId));
        verify(restaurantRepository).findById(restaurantId);
        verify(userService).getUserEntityById(ownerId);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }
}
