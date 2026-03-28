package com.vaibhav.product_service.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;

    // optional but good for real-world
    private String address;

}
