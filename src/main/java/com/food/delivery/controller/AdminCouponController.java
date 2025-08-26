package com.food.delivery.controller;

import com.food.delivery.dto.CouponRequest;
import com.food.delivery.dto.CouponResponse;
import com.food.delivery.service.CouponService;
import com.food.delivery.constants.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/coupons")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminCouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/create")
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request,
                                                      Authentication authentication) {
        UUID adminId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        log.info("Admin coupon creation request received by admin: {} with code: {}", adminId, request.getCode());
        try {
            CouponResponse response = couponService.createCoupon(request, adminId);
            log.info("Admin coupon created successfully with ID: {} by admin: {}", response.getId(), adminId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Admin coupon creation failed by admin: {} - Error: {}", adminId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Page<CouponResponse>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Admin get all coupons request received - page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CouponResponse> response = couponService.getAllCoupons(pageable);
            log.info("Retrieved {} coupons for admin", response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve coupons for admin - Error: {}", e.getMessage(), e);
            throw e;
        }
    }
}
