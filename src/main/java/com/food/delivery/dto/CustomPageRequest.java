package com.food.delivery.dto;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Value
@Builder
public class CustomPageRequest {
    @Builder.Default Integer page = 0;
    @Builder.Default Integer size = 20;
    @Builder.Default String sort = "createdAt";
    @Builder.Default String direction = "desc";

    public Pageable toPageable() {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }
}
