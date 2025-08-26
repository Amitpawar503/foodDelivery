package com.food.delivery.service.impl;

import com.food.delivery.entity.Restaurant;
import com.food.delivery.entity.User;
import com.food.delivery.entity.UserRestaurantBlock;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.UserRestaurantBlockRepository;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.service.UserRestaurantBlockService;
import com.food.delivery.service.UserService;
import com.food.delivery.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class UserRestaurantBlockServiceImpl implements UserRestaurantBlockService {

    @Autowired
    private UserRestaurantBlockRepository blockRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    @Override
    public void blockUser(UUID restaurantId, UUID userId, UUID ownerId) {
        Restaurant restaurant = restaurantService.getRestaurantEntityById(restaurantId);
        User owner = userService.getUserEntityById(ownerId);
        User userToBlock = userService.getUserEntityById(userId);

        // Check if user is owner or admin
        if (owner.getRole() != UserRole.ADMIN && 
            !restaurant.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        // Check if user is already blocked
        if (blockRepository.existsByUserIdAndRestaurantId(userId, restaurantId)) {
            log.info("User {} is already blocked from restaurant {}", userId, restaurantId);
            return;
        }

        UserRestaurantBlock block = new UserRestaurantBlock();
        block.setUser(userToBlock);
        block.setRestaurant(restaurant);
        block.setBlockedBy(owner);
        block.setBlockedAt(LocalDateTime.now());

        blockRepository.save(block);
        log.info("User {} blocked from restaurant {} by owner {}", userId, restaurantId, ownerId);
    }

    @Override
    public void unblockUser(UUID restaurantId, UUID userId, UUID ownerId) {
        Restaurant restaurant = restaurantService.getRestaurantEntityById(restaurantId);
        User owner = userService.getUserEntityById(ownerId);

        // Check if user is owner or admin
        if (owner.getRole() != UserRole.ADMIN && 
            !restaurant.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException(ErrorConstants.INSUFFICIENT_PERMISSIONS_MESSAGE);
        }

        blockRepository.findByUserIdAndRestaurantId(userId, restaurantId)
                .ifPresent(blockRepository::delete);
        
        log.info("User {} unblocked from restaurant {} by owner {}", userId, restaurantId, ownerId);
    }

    @Override
    public boolean isUserBlocked(UUID restaurantId, UUID userId) {
        return blockRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }
}
