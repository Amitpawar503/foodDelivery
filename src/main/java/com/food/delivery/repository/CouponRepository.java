package com.food.delivery.repository;

import com.food.delivery.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByCode(String code);
    
    boolean existsByCode(String code);

    Object findByExpiresAtBeforeAndActiveTrue(LocalDateTime threshold);
}
