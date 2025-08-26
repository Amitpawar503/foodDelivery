package com.food.delivery.repository;

import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    // Basic finder methods using Spring Data JPA naming conventions
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    Page<User> findByBlocked(Boolean blocked, Pageable pageable);
    
    Page<User> findByRoleAndBlocked(UserRole role, Boolean blocked, Pageable pageable);
    
    // Custom finder methods for common queries
    Page<User> findByBlockedFalse(Pageable pageable);
    
    Page<User> findByRoleAndBlockedFalse(UserRole role, Pageable pageable);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByBlocked(Boolean blocked);
    
    boolean existsByRole(UserRole role);
    
    long countByRole(UserRole role);
    
    long countByBlocked(Boolean blocked);
}
