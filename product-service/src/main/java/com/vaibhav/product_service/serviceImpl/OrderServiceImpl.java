package com.vaibhav.product_service.serviceImpl;

import com.vaibhav.product_service.dto.OrderRequest;
import com.vaibhav.product_service.dto.OrderResponse;
import com.vaibhav.product_service.dto.OrderStatus;
import com.vaibhav.product_service.entity.Order;
import com.vaibhav.product_service.entity.Product;
import com.vaibhav.product_service.repository.OrderRepository;
import com.vaibhav.product_service.repository.ProductRepository;
import com.vaibhav.product_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    // 1. validate product
    // 2. reduce stock
    // 3. save order
    // 4. return response
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            return new OrderResponse(
                    null,
                    request.getProductId(),
                    request.getQuantity(),
                    OrderStatus.OUT_OF_STOCK.toString(),
                    "Not enough stock",
                    null
            );
        }

        // reduce stock
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        // create order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getBasePrice().multiply(BigDecimal.valueOf(request.getQuantity())).doubleValue());
        order.setStatus(OrderStatus.SUCCESS);
        order.setAddress(request.getAddress());
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus().toString(),
                "Order placed successfully",
                order.getTotalPrice()
        );
    }
}
