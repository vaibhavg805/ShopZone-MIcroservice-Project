package com.vaibhav.product_service.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BrandResponseDto {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean isActive;
}
