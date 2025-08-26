package com.food.delivery.service;

import com.food.delivery.dto.CouponRequest;
import com.food.delivery.dto.CouponResponse;
import com.food.delivery.entity.Coupon;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.CouponRepository;
import com.food.delivery.service.impl.CouponServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CouponServiceImpl couponService;

    private User adminUser;
    private Coupon testCoupon;
    private UUID adminId;
    private UUID couponId;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        couponId = UUID.randomUUID();

        adminUser = new User();
        adminUser.setId(adminId);
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);

        testCoupon = new Coupon();
        testCoupon.setId(couponId);
        testCoupon.setCode("TEST20");
        testCoupon.setDiscountPercent(20);
        testCoupon.setActive(true);
        testCoupon.setExpiresAt(LocalDateTime.now().plusDays(30));
    }

    @Test
    void createCoupon_ValidRequest_ReturnsCouponResponse() {
        // Arrange
        CouponRequest request = new CouponRequest();
        request.setCode("NEW20");
        request.setDiscountPercent(20);
        request.setExpiresAt(LocalDateTime.now().plusDays(30));

        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(couponRepository.existsByCode("NEW20")).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        // Act
        CouponResponse result = couponService.createCoupon(request, adminId);

        // Assert
        assertNotNull(result);
        assertEquals(couponId, result.getId());
        assertEquals("TEST20", result.getCode());
        assertEquals(20, result.getDiscountPercent());
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void createCoupon_NonAdminUser_ThrowsException() {
        // Arrange
        User nonAdminUser = new User();
        nonAdminUser.setId(UUID.randomUUID());
        nonAdminUser.setRole(UserRole.CUSTOMER);

        CouponRequest request = new CouponRequest();
        request.setCode("NEW20");

        when(userService.getUserEntityById(nonAdminUser.getId())).thenReturn(nonAdminUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> couponService.createCoupon(request, nonAdminUser.getId()));
        assertEquals(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE, exception.getMessage());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void createCoupon_ExistingCode_ThrowsException() {
        // Arrange
        CouponRequest request = new CouponRequest();
        request.setCode("EXISTING");

        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(couponRepository.existsByCode("EXISTING")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> couponService.createCoupon(request, adminId));
        assertEquals(ErrorConstants.COUPON_CODE_EXISTS_MESSAGE, exception.getMessage());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void getCouponById_ExistingCoupon_ReturnsCouponResponse() {
        // Arrange
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));

        // Act
        CouponResponse result = couponService.getCouponById(couponId);

        // Assert
        assertNotNull(result);
        assertEquals(couponId, result.getId());
        assertEquals("TEST20", result.getCode());
        verify(couponRepository).findById(couponId);
    }

    @Test
    void getCouponById_NonExistingCoupon_ThrowsException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(couponRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> couponService.getCouponById(nonExistingId));
        assertEquals(ErrorConstants.COUPON_NOT_FOUND_MESSAGE + ": " + nonExistingId, exception.getMessage());
        verify(couponRepository).findById(nonExistingId);
    }

    @Test
    void getCouponByCode_ExistingCoupon_ReturnsCouponResponse() {
        // Arrange
        when(couponRepository.findByCode("TEST20")).thenReturn(Optional.of(testCoupon));

        // Act
        CouponResponse result = couponService.getCouponByCode("TEST20");

        // Assert
        assertNotNull(result);
        assertEquals(couponId, result.getId());
        assertEquals("TEST20", result.getCode());
        verify(couponRepository).findByCode("TEST20");
    }

    @Test
    void getCouponByCode_NonExistingCoupon_ThrowsException() {
        // Arrange
        when(couponRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> couponService.getCouponByCode("NONEXISTENT"));
        assertEquals(ErrorConstants.COUPON_NOT_FOUND_MESSAGE + ": NONEXISTENT", exception.getMessage());
        verify(couponRepository).findByCode("NONEXISTENT");
    }

    @Test
    void getAllCoupons_ReturnsPageOfCoupons() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Coupon> coupons = List.of(testCoupon);
        Page<Coupon> couponPage = new PageImpl<>(coupons, pageable, coupons.size());

        when(couponRepository.findAll(pageable)).thenReturn(couponPage);

        // Act
        Page<CouponResponse> result = couponService.getAllCoupons(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(couponId, result.getContent().get(0).getId());
        verify(couponRepository).findAll(pageable);
    }

    @Test
    void updateCoupon_ValidRequest_ReturnsUpdatedCoupon() {
        // Arrange
        CouponRequest request = new CouponRequest();
        request.setCode("UPDATED20");
        request.setDiscountPercent(25);

        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setId(couponId);
        updatedCoupon.setCode("UPDATED20");
        updatedCoupon.setDiscountPercent(25);

        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(updatedCoupon);

        // Act
        CouponResponse result = couponService.updateCoupon(couponId, request, adminId);

        // Assert
        assertNotNull(result);
        assertEquals(couponId, result.getId());
        assertEquals("UPDATED20", result.getCode());
        assertEquals(25, result.getDiscountPercent());
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void deleteCoupon_ExistingCoupon_DeletesCoupon() {
        // Arrange
        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
        doNothing().when(couponRepository).deleteById(couponId);

        // Act
        couponService.deleteCoupon(couponId, adminId);

        // Assert
        verify(couponRepository).findById(couponId);
        verify(couponRepository).deleteById(couponId);
    }

    @Test
    void activateCoupon_ExistingCoupon_ActivatesCoupon() {
        // Arrange
        testCoupon.setActive(false);
        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        // Act
        couponService.activateCoupon(couponId, adminId);

        // Assert
        verify(couponRepository).findById(couponId);
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void deactivateCoupon_ExistingCoupon_DeactivatesCoupon() {
        // Arrange
        when(userService.getUserEntityById(adminId)).thenReturn(adminUser);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        // Act
        couponService.deactivateCoupon(couponId, adminId);

        // Assert
        verify(couponRepository).findById(couponId);
        verify(couponRepository).save(any(Coupon.class));
    }
}
