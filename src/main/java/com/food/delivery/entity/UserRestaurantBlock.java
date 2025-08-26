package com.food.delivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_restaurant_blocks")
@EqualsAndHashCode(callSuper = true)
public class UserRestaurantBlock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_by", nullable = false)
    private User blockedBy;

    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;
}
