package com.vaibhav.product_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;
}
