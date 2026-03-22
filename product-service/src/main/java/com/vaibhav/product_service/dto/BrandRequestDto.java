package com.vaibhav.product_service.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BrandRequestDto {
    private String name;
    private String description;
    private String logoUrl;
    private Boolean isActive;
}