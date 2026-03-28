package com.vaibhav.product_service.controller;

import com.vaibhav.product_service.dto.OrderRequest;
import com.vaibhav.product_service.dto.OrderResponse;
import com.vaibhav.product_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
       OrderResponse response = orderService.placeOrder(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
