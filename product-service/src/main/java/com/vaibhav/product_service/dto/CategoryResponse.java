package com.vaibhav.product_service.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record CategoryResponse(Long id,
                               String name,
                               String description,
                               String imageUrl,
                               Boolean isActive,
                               Long parentId,
                               List<CategoryResponse> children)
{}
