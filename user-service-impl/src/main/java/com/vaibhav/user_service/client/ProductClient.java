package com.vaibhav.user_service.client;

import com.vaibhav.user_service.client.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {
    // call product protected APIS
    @PostMapping("/products/test")
    String testApi(@RequestBody String data);

    @GetMapping("/products")
    List<ProductResponseDto> getAllProducts();
}
