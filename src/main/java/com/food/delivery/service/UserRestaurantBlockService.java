package com.food.delivery.service;

import java.util.UUID;

public interface UserRestaurantBlockService {

    void blockUser(UUID restaurantId, UUID userId, UUID ownerId);

    void unblockUser(UUID restaurantId, UUID userId, UUID ownerId);

    boolean isUserBlocked(UUID restaurantId, UUID userId);
}
