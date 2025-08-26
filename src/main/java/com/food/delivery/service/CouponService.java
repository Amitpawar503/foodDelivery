package com.food.delivery.service;

import com.food.delivery.dto.CouponRequest;
import com.food.delivery.dto.CouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CouponService {

    CouponResponse createCoupon(CouponRequest request, UUID adminId);

    CouponResponse getCouponById(UUID couponId);

    CouponResponse getCouponByCode(String code);

    Page<CouponResponse> getAllCoupons(Pageable pageable);

    Page<CouponResponse> getActiveCoupons(Pageable pageable);

    CouponResponse updateCoupon(UUID couponId, CouponRequest request, UUID adminId);

    void deleteCoupon(UUID couponId, UUID adminId);

    void activateCoupon(UUID couponId, UUID adminId);

    void deactivateCoupon(UUID couponId, UUID adminId);

    boolean validateCoupon(String code);

    List<CouponResponse> getExpiringCoupons(int days);
}
