package com.food.delivery.repository;

import com.food.delivery.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID>, JpaSpecificationExecutor<Restaurant> {

    // Basic finder methods using Spring Data JPA naming conventions
    Page<Restaurant> findByBlocked(Boolean blocked, Pageable pageable);
    
    Page<Restaurant> findByOwnerId(UUID ownerId, Pageable pageable);
    
    List<Restaurant> findByOwnerIdAndBlocked(UUID ownerId, Boolean blocked);
    
    Optional<Restaurant> findByName(String name);
    
    // Custom finder methods for common queries
    Page<Restaurant> findByBlockedFalse(Pageable pageable);
    
    List<Restaurant> findByOwnerId(UUID ownerId);
    
    boolean existsByName(String name);
    
    boolean existsByOwnerId(UUID ownerId);
}
