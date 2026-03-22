package com.vaibhav.product_service.dto;

public record CategoryRequest(
        String name,
        String description,
        String imageUrl,
        Boolean isActive,
        Long parentId
) {}
