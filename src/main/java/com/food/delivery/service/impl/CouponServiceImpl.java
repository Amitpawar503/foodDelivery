package com.food.delivery.service.impl;

import com.food.delivery.dto.CouponRequest;
import com.food.delivery.dto.CouponResponse;
import com.food.delivery.entity.Coupon;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.CouponRepository;
import com.food.delivery.service.CouponService;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserService userService;

    @Override
    public CouponResponse createCoupon(CouponRequest request, UUID adminId) {
        User admin = userService.getUserEntityById(adminId);
        
        // Only admin can create coupons
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        if (couponRepository.existsByCode(request.getCode())) {
            throw new RuntimeException(ErrorConstants.COUPON_CODE_EXISTS_MESSAGE);
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDiscountPercent(request.getDiscountPercent());
        coupon.setExpiresAt(request.getExpiresAt());
        coupon.setActive(request.getActive() != null ? request.getActive() : Boolean.TRUE);

        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Created coupon: {} by admin: {}", savedCoupon.getCode(), admin.getEmail());

        return mapToCouponResponse(savedCoupon);
    }

    @Override
    public CouponResponse getCouponById(UUID couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.COUPON_NOT_FOUND_MESSAGE + ": " + couponId));

        return mapToCouponResponse(coupon);
    }

    @Override
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.COUPON_NOT_FOUND_MESSAGE + ": " + code));

        return mapToCouponResponse(coupon);
    }

    @Override
    public Page<CouponResponse> getAllCoupons(Pageable pageable) {
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        return coupons.map(this::mapToCouponResponse);
    }

    @Override
    public Page<CouponResponse> getActiveCoupons(Pageable pageable) {
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        // Filter active coupons in memory since repository doesn't have findByActiveTrue
        List<Coupon> activeCoupons = coupons.getContent().stream()
                .filter(coupon -> Boolean.TRUE.equals(coupon.getActive()))
                .toList();
        
        // Create a new page with filtered content
        Page<Coupon> activeCouponsPage = new org.springframework.data.domain.PageImpl<>(
                activeCoupons, pageable, activeCoupons.size());
        
        return activeCouponsPage.map(this::mapToCouponResponse);
    }

    @Override
    public CouponResponse updateCoupon(UUID couponId, CouponRequest request, UUID adminId) {
        User admin = userService.getUserEntityById(adminId);
        
        // Only admin can update coupons
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));

        if (request.getCode() != null && !request.getCode().equals(coupon.getCode())) {
            if (couponRepository.existsByCode(request.getCode())) {
                throw new RuntimeException("Coupon code already exists");
            }
            coupon.setCode(request.getCode());
        }
        if (request.getDiscountPercent() != null) {
            coupon.setDiscountPercent(request.getDiscountPercent());
        }
        if (request.getExpiresAt() != null) {
            coupon.setExpiresAt(request.getExpiresAt());
        }
        if (request.getActive() != null) {
            coupon.setActive(request.getActive());
        }

        Coupon updatedCoupon = couponRepository.save(coupon);
        log.info("Updated coupon: {} by admin: {}", updatedCoupon.getCode(), admin.getEmail());

        return mapToCouponResponse(updatedCoupon);
    }

    @Override
    public void deleteCoupon(UUID couponId, UUID adminId) {
        User admin = userService.getUserEntityById(adminId);
        
        // Only admin can delete coupons
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admin can delete coupons");
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));

        couponRepository.delete(coupon);
        log.info("Deleted coupon: {} by admin: {}", coupon.getCode(), admin.getEmail());
    }

    @Override
    public void activateCoupon(UUID couponId, UUID adminId) {
        User admin = userService.getUserEntityById(adminId);
        
        // Only admin can activate coupons
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admin can activate coupons");
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));

        coupon.setActive(true);
        couponRepository.save(coupon);
        log.info("Activated coupon: {} by admin: {}", coupon.getCode(), admin.getEmail());
    }

    @Override
    public void deactivateCoupon(UUID couponId, UUID adminId) {
        User admin = userService.getUserEntityById(adminId);
        
        // Only admin can deactivate coupons
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admin can deactivate coupons");
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));

        coupon.setActive(false);
        couponRepository.save(coupon);
        log.info("Deactivated coupon: {} by admin: {}", coupon.getCode(), admin.getEmail());
    }

    @Override
    public boolean validateCoupon(String code) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        if (couponOpt.isEmpty()) {
            return false;
        }

        Coupon coupon = couponOpt.get();
        return coupon.isValid();
    }

    @Override
    public List<CouponResponse> getExpiringCoupons(int days) {
        LocalDateTime threshold = LocalDateTime.now().plusDays(days);
        List<Coupon> allCoupons = couponRepository.findAll();
        
        // Filter expiring coupons in memory since repository doesn't have findByExpiresAtBeforeAndActiveTrue
        List<Coupon> expiringCoupons = allCoupons.stream()
                .filter(coupon -> Boolean.TRUE.equals(coupon.getActive()) && 
                                 coupon.getExpiresAt() != null && 
                                 coupon.getExpiresAt().isBefore(threshold))
                .toList();
        
        return expiringCoupons.stream()
                .map(this::mapToCouponResponse)
                .toList();
    }

    private CouponResponse mapToCouponResponse(Coupon coupon) {
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
}
