package com.vaibhav.product_service.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private Long brandId;
    private BigDecimal basePrice;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;
    private String sku;
    private Boolean isActive;
    private String specifications;
    private BigDecimal averageRating;
    private Integer reviewCount;
}
