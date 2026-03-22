package com.vaibhav.product_service.dto;

import lombok.*;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductRequestDto {
    private String name;
    private String description;
    private Long categoryId;
    private Long brandId;
    private BigDecimal basePrice;
    private BigDecimal discountPercentage;
    private String sku;
    private Boolean isActive;
    private String specifications;
}
