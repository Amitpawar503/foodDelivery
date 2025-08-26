package com.food.delivery.repository;

import com.food.delivery.entity.UserRestaurantBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRestaurantBlockRepository extends JpaRepository<UserRestaurantBlock, UUID> {

    Optional<UserRestaurantBlock> findByUserIdAndRestaurantId(UUID userId, UUID restaurantId);
    
    boolean existsByUserIdAndRestaurantId(UUID userId, UUID restaurantId);
    
    List<UserRestaurantBlock> findByUserId(UUID userId);
    
    List<UserRestaurantBlock> findByRestaurantId(UUID restaurantId);
    
    @Query("SELECT urb FROM UserRestaurantBlock urb WHERE urb.user.id = :userId AND urb.restaurant.id = :restaurantId")
    Optional<UserRestaurantBlock> findBlockByUserAndRestaurant(
        @Param("userId") UUID userId,
        @Param("restaurantId") UUID restaurantId
    );
}
