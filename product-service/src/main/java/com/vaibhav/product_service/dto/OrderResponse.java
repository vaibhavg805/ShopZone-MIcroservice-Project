package com.vaibhav.product_service.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderResponse {

    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String status;
    private String message;

    // optional (good for future)
    private Double totalPrice;

}
