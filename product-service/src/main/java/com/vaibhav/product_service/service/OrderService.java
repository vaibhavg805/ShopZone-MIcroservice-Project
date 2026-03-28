package com.vaibhav.product_service.service;

import com.vaibhav.product_service.dto.OrderRequest;
import com.vaibhav.product_service.dto.OrderResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService {
    public OrderResponse placeOrder(OrderRequest request);
}
