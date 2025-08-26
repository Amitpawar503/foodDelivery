package com.food.delivery.dto;

import com.food.delivery.enums.UserRole;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String name;
    private UserRole role;
    private Boolean blocked;
}
