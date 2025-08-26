package com.food.delivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "coupons")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Coupon extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Order> orders;

    public boolean isValid() {
        return Boolean.TRUE.equals(active) && (expiresAt == null || LocalDateTime.now().isBefore(expiresAt));
    }
}
